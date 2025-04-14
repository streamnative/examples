#include <librdkafka/rdkafka.h>
#include <unistd.h>

#include <csignal>
#include <cstdlib>
#include <iostream>
#include <string>

/* Typical include path is <libserdes/serdes.h> */
extern "C" {
#include <libserdes/serdes-avro.h>
}

#include "config_parser.h"

// Use the User schema definition
const char *user_schema_def =
    "{"
    "    \"name\": \"User\","
    "    \"type\": \"record\","
    "    \"fields\": ["
    "        {"
    "            \"name\": \"name\","
    "            \"type\": \"string\""
    "        },"
    "        {"
    "            \"name\": \"age\","
    "            \"type\": \"int\""
    "        }"
    "    ]"
    "}";

static int run = 1;
static int verbosity = 2;

#define FATAL(reason...)               \
  do {                                 \
    fprintf(stderr, "FATAL: " reason); \
    exit(1);                           \
  } while (0)

void produce_user_message(rd_kafka_topic_t *rkt, int32_t partition,
                          serdes_schema_t *serdes_schema, int verbosity) {
  avro_value_t field;
  void *ser_buf = nullptr;
  size_t ser_buf_size;
  char errstr[512];

  // Create a new User record
  avro_schema_t schema = serdes_schema_avro(serdes_schema);
  avro_value_iface_t *record_class = avro_generic_class_from_schema(schema);

  avro_value_t record;
  avro_generic_value_new(record_class, &record);

  // Set name field
  if (avro_value_get_by_name(&record, "name", &field, nullptr) == 0) {
    avro_value_set_string(&field, "John Doe");
  }

  // Set age field
  if (avro_value_get_by_name(&record, "age", &field, nullptr) == 0) {
    avro_value_set_int(&field, 30);
  }

  // Serialize and produce
  if (serdes_schema_serialize_avro(serdes_schema, &record, &ser_buf,
                                   &ser_buf_size, errstr, sizeof(errstr))) {
    fprintf(stderr, "%% serialize_avro() failed: %s\n", errstr);
    avro_value_decref(&record);
    return;
  }

  if (rd_kafka_produce(rkt, partition, RD_KAFKA_MSG_F_FREE, ser_buf,
                       ser_buf_size, nullptr, 0, nullptr) == -1) {
    fprintf(stderr, "%% Failed to produce message: %s\n",
            rd_kafka_err2str(rd_kafka_last_error()));
    free(ser_buf);
  } else {
    fprintf(stderr,
            "%% Successfully produced User record (name: 'John Doe', age: 30) "
            "of %zd bytes\n",
            ser_buf_size);
  }

  avro_value_decref(&record);
}

static void dr_msg_cb(rd_kafka_t *rk, const rd_kafka_message_t *rkmessage,
                      void *opaque) {
  if (rkmessage->err) {
    fprintf(stderr, "%% Message delivery failed: %s\n",
            rd_kafka_err2str(rkmessage->err));
  } else {
    fprintf(stderr, "%% Message delivered (%zd bytes, partition %d)\n",
            rkmessage->len, rkmessage->partition);
  }
}

static void run_producer(rd_kafka_conf_t *rk_conf,
                         rd_kafka_topic_conf_t *rkt_conf, const char *topic,
                         int32_t partition, const char *schema_name,
                         int schema_id, const char *schema_def,
                         serdes_t *serdes) {
  rd_kafka_t *rk;
  rd_kafka_topic_t *rkt;
  char errstr[512];
  serdes_schema_t *schema = nullptr;

  schema = serdes_schema_add(serdes, schema_name, schema_id, schema_def, -1,
                             errstr, sizeof(errstr));
  if (!schema) FATAL("Failed to register schema: %s\n", errstr);

  if (verbosity >= 1)
    fprintf(stdout, "%% Added schema %s with id %d\n",
            serdes_schema_name(schema), serdes_schema_id(schema));

  // Set delivery report callback before creating producer
  rd_kafka_conf_set_dr_msg_cb(rk_conf, dr_msg_cb);

  rk = rd_kafka_new(RD_KAFKA_PRODUCER, rk_conf, errstr, sizeof(errstr));
  if (!rk) FATAL("%% Failed to create producer: %s\n", errstr);

  rkt = rd_kafka_topic_new(rk, topic, rkt_conf);

  // Produce a User message automatically
  fprintf(stdout, "%% Producing a sample User message to topic %s\n", topic);
  produce_user_message(rkt, partition, schema, verbosity);
  for (int i = 0; i < 10 && run; i++) {
    rd_kafka_poll(rk, 100);  // Poll every 100ms
  }
  rd_kafka_resp_err_t flush_result = rd_kafka_flush(rk, 10000);
  if (flush_result == RD_KAFKA_RESP_ERR__TIMED_OUT) {
    fprintf(stderr,
            "%% Failed to flush all messages after 10s: %d messages remain\n",
            rd_kafka_outq_len(rk));
  } else if (flush_result != RD_KAFKA_RESP_ERR_NO_ERROR) {
    fprintf(stderr, "%% Failed to flush messages: %s\n",
            rd_kafka_err2str(flush_result));
  } else {
    fprintf(stdout, "%% Flushing final messages...\n");
  }

  if (verbosity >= 1) fprintf(stdout, "%% Flushed all messages\n");

  rd_kafka_topic_destroy(rkt);
  rd_kafka_destroy(rk);
}
static void sig_term(int sig) {
  run = 0;
  fclose(stdin);
}

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
  ConfigParser config;
  if (argc < 2) {
    const char *default_config = "sncloud.ini";
    fprintf(stderr, "No config file specified, using default: %s\n",
            default_config);

    // Check if default config exists
    if (access(default_config, F_OK) != 0) {
      fprintf(stderr, "Default config file not found. Usage: %s <config.ini>\n",
              argv[0]);
      return 1;
    }

    // Use default config file
    if (!config.parse(default_config)) {
      fprintf(stderr, "Failed to parse default config file: %s\n",
              default_config);
      return 1;
    }
  } else {
    // Use specified config file
    if (!config.parse(argv[1])) {
      fprintf(stderr, "Failed to parse config file: %s\n", argv[1]);
      return 1;
    }
  }

  const std::string topic = config.get("common", "topic", "test");
  if (topic.empty()) {
    fprintf(stderr, "Error: 'topic' cannot be empty in configuration\n");
    return 1;
  }
  const std::string bootstrap_servers =
      config.get("common", "bootstrap.servers", "localhost:9092");
  if (bootstrap_servers.empty()) {
    fprintf(stderr,
            "Error: 'bootstrap.servers' cannot be empty in configuration\n");
    return 1;
  }
  const std::string token = config.get("common", "token", "");
  const std::string schema_registry_url =
      config.get("schema.registry", "url", "http://localhost:8081");
  if (schema_registry_url.empty()) {
    fprintf(stderr,
            "Error: 'schema.registry.url' cannot be empty in configuration\n");
    return 1;
  }
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

  serdes_conf_t *sconf = serdes_conf_new(
      nullptr, 0, "schema.registry.url",
      format_schema_registry_url(schema_registry_url, token).c_str(), NULL);

  serdes_t *serdes = serdes_new(sconf, errstr, sizeof(errstr));
  if (!serdes) {
    fprintf(stderr, "%% Failed to create serdes handle: %s\n", errstr);
    exit(1);
  }

  std::string schema_name_str = topic + "-value";
  const char *schema_name = schema_name_str.c_str();
  int schema_id = -1;

  run_producer(rk_conf, rkt_conf, topic.c_str(), partition, schema_name,
               schema_id, user_schema_def, serdes);

  serdes_destroy(serdes);
  rd_kafka_wait_destroyed(5000);

  return 0;
}
