#include <inttypes.h>  // For PRId64
#include <librdkafka/rdkafka.h>
#include <unistd.h>

#include <cstdlib>
#include <string>

#include "config.h"

/* Typical include path is <libserdes/serdes.h> */
extern "C" {
#include <libserdes/serdes-avro.h>
}

static int run = 1;
static int exit_eof = 0;
static int verbosity = 2;
static int message_received = 0;  // Flag to track if we've received a message

#define FATAL(reason...)               \
  do {                                 \
    fprintf(stderr, "FATAL: " reason); \
    exit(1);                           \
  } while (0)

/**
 * Parse, deserialize and print a consumed message.
 * Returns 1 if a valid User message was processed, 0 otherwise.
 */
static int parse_msg(rd_kafka_message_t *rkmessage, serdes_t *serdes) {
  avro_value_t avro;
  serdes_err_t err;
  serdes_schema_t *schema;
  char errstr[512];
  char *as_json;

  /* Automatic deserialization using message framing */
  err = serdes_deserialize_avro(serdes, &avro, &schema, rkmessage->payload,
                                rkmessage->len, errstr, sizeof(errstr));
  if (err) {
    fprintf(stderr, "%% serdes_deserialize_avro failed: %s\n", errstr);
    return 0;
  }

  if (verbosity > 1)
    fprintf(stderr,
            "%% Successful Avro deserialization using "
            "schema %s id %d\n",
            serdes_schema_name(schema), serdes_schema_id(schema));

  /* Convert to JSON and print */
  if (avro_value_to_json(&avro, 1, &as_json))
    fprintf(stderr, "%% avro_to_json failed: %s\n", avro_strerror());
  else {
    printf("%% Successful convert to JSON: %s\n", as_json);

    // Check if this is a User message with name and age fields
    int is_user_message = 0;
    avro_value_t field;
    const char *name_value;
    int32_t age_value;

    if (avro_value_get_by_name(&avro, "name", &field, NULL) == 0 &&
        avro_value_get_by_name(&avro, "age", &field, NULL) == 0) {
      is_user_message = 1;

      // Get the name field
      if (avro_value_get_by_name(&avro, "name", &field, NULL) == 0) {
        size_t name_size;
        avro_value_get_string(&field, &name_value, &name_size);

        // Get the age field
        if (avro_value_get_by_name(&avro, "age", &field, NULL) == 0) {
          avro_value_get_int(&field, &age_value);
          fprintf(stderr, "%% Received User message - Name: %s, Age: %d\n",
                  name_value, age_value);
        }
      }
    }

    free(as_json);
    avro_value_decref(&avro);
    return is_user_message;
  }

  avro_value_decref(&avro);
  return 0;
}

static void run_consumer(rd_kafka_conf_t *rk_conf,
                         rd_kafka_topic_conf_t *rkt_conf, const char *topic,
                         int32_t partition, serdes_t *serdes,
                         const char *group_id) {
  rd_kafka_t *rk;
  rd_kafka_topic_t *rkt;
  char errstr[512];
  rd_kafka_message_t *rkmessage;
  int max_attempts = 10;  // Maximum number of attempts to wait for message
  int attempts = 0;

  rk = rd_kafka_new(RD_KAFKA_CONSUMER, rk_conf, errstr, sizeof(errstr));
  if (!rk) FATAL("Failed to create consumer: %s\n", errstr);

  rkt = rd_kafka_topic_new(rk, topic, rkt_conf);

  if (group_id && *group_id) {
    fprintf(stderr, "%% Using consumer group: %s\n", group_id);
  }

  rd_kafka_consume_start(rkt, partition, RD_KAFKA_OFFSET_BEGINNING);

  fprintf(stderr, "%% Waiting for User message...\n");

  while (run && !message_received && attempts < max_attempts) {
    rkmessage = rd_kafka_consume(rkt, partition, 1000);  // 1 second timeout
    if (!rkmessage) {
      attempts++;
      fprintf(stderr, "%% Waiting for message... (%d/%d)\n", attempts,
              max_attempts);
      continue;
    }

    if (rkmessage->err) {
      if (rkmessage->err == RD_KAFKA_RESP_ERR__PARTITION_EOF) {
        fprintf(stderr, "%% Reached end of partition\n");
        if (exit_eof) run = 0;
      } else {
        fprintf(stderr,
                "%% Consumed message (offset %" PRId64
                ") "
                "error: %s\n",
                rkmessage->offset, rd_kafka_message_errstr(rkmessage));
      }
    } else {
      // Process the message and check if it's a User message
      if (parse_msg(rkmessage, serdes)) {
        message_received = 1;
        fprintf(stderr, "%% Successfully received a User message, exiting\n");
        run = 0;
      } else {
        fprintf(stderr, "%% Received a non-User message, continuing...\n");
      }
    }

    rd_kafka_message_destroy(rkmessage);
    attempts = 0;  // Reset attempts counter when we get any message
  }

  if (!message_received) {
    if (attempts >= max_attempts) {
      fprintf(stderr, "%% Timeout waiting for User message\n");
    }
  }

  rd_kafka_consume_stop(rkt, partition);

  run = 1;
  while (run && rd_kafka_outq_len(rk) > 0) usleep(100 * 1000);

  rd_kafka_topic_destroy(rkt);
  rd_kafka_destroy(rk);
}

static void sig_term(int sig) { run = 0; }

std::string format_schema_registry_url(const std::string &url,
                                       const std::string &token) {
  // If no token or URL already has authentication info, return as is
  if (token.empty() || url.find('@') != std::string::npos) {
    return url;
  }

  size_t protocol_end = url.find("://");
  if (protocol_end == std::string::npos) {
    // No protocol in URL, can't safely modify
    return url;
  }

  std::string protocol = url.substr(0, protocol_end);
  std::string remaining = url.substr(protocol_end + 3);

  // Format: protocol://user:token@remaining
  return protocol + "://user:" + token + "@" + remaining;
}

int main(int argc, char **argv) {
  Config config(argc > 1 ? argv[1] : "sncloud.ini");
  const auto bootstrap_servers = config.bootstrap_servers();
  const auto topic = config.topic();
  const auto schema_registry_url = config.schema_registry_url();
  const auto token = config.token();
  const auto group_id = config.group_id();
  int partition = 0;

  signal(SIGINT, sig_term);
  signal(SIGTERM, sig_term);

  rd_kafka_conf_t *rk_conf = rd_kafka_conf_new();
  rd_kafka_topic_conf_t *rkt_conf = rd_kafka_topic_conf_new();
  char errstr[512];

  if (rd_kafka_conf_set(rk_conf, "bootstrap.servers", bootstrap_servers.c_str(),
                        errstr, sizeof(errstr)) != RD_KAFKA_CONF_OK) {
    fprintf(stderr, "%s\n", errstr);
    return 1;
  }

  // Set token if provided
  if (!token.empty()) {
    if (rd_kafka_conf_set(rk_conf, "sasl.username", "user", errstr,
                          sizeof(errstr)) != RD_KAFKA_CONF_OK ||
        rd_kafka_conf_set(rk_conf, "sasl.password", ("token:" + token).c_str(),
                          errstr, sizeof(errstr)) != RD_KAFKA_CONF_OK ||
        rd_kafka_conf_set(rk_conf, "sasl.mechanism", "PLAIN", errstr,
                          sizeof(errstr)) != RD_KAFKA_CONF_OK ||
        rd_kafka_conf_set(rk_conf, "security.protocol", "SASL_SSL", errstr,
                          sizeof(errstr)) != RD_KAFKA_CONF_OK) {
      fprintf(stderr, "Failed to configure SASL authentication: %s\n", errstr);
      return 1;
    }
  }

  // For auto.offset.reset
  if (rd_kafka_topic_conf_set(rkt_conf, "auto.offset.reset", "earliest", errstr,
                              sizeof(errstr)) != RD_KAFKA_CONF_OK) {
    fprintf(stderr, "%s\n", errstr);
    return 1;
  }

  serdes_conf_t *sconf = serdes_conf_new(
      nullptr, 0, "schema.registry.url",
      format_schema_registry_url(schema_registry_url, token).c_str(), NULL);

  serdes_t *serdes;
  serdes = serdes_new(sconf, errstr, sizeof(errstr));
  if (!serdes) {
    fprintf(stderr, "%% Failed to create serdes handle: %s\n", errstr);
    exit(1);
  }

  fprintf(stderr, "%% Starting consumer for topic %s (broker(s) %s)\n",
          topic.c_str(), bootstrap_servers.c_str());
  fprintf(stderr, "%% Using schema registry at %s\n",
          schema_registry_url.c_str());
  fprintf(stderr, "%% Will exit after receiving one User message or timeout\n");

  run_consumer(rk_conf, rkt_conf, topic.c_str(), partition, serdes,
               group_id.c_str());

  serdes_destroy(serdes);
  rd_kafka_wait_destroyed(5000);

  return message_received ? 0 : 1;  // Return success if we received a message
}
