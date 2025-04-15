#include <stdio.h>

#include <memory>
#include <stdexcept>
#include <string>

#include "config.h"
#include "librdkafka/rdkafka.h"
extern "C" {
#include "libserdes/serdes-avro.h"
}
#include "utils.h"

inline User deserialize(serdes_t *serdes, void *payload, size_t len) {
  char errstr[512];
  avro_value_t avro;
  serdes_schema_t *schema;
  auto err = serdes_deserialize_avro(serdes, &avro, &schema, payload, len, errstr, sizeof(errstr));
  if (err) {
    fail("serdes_deserialize_avro failed: " + std::string(errstr));
  }
  std::unique_ptr<avro_value_t, decltype(&avro_value_decref)> avro_guard{&avro, &avro_value_decref};

  const char *name;
  size_t name_size;
  int age;
  avro_value_t field;
  if (avro_value_get_by_name(&avro, "name", &field, nullptr) != 0) {
    fail("no name field");
  }
  avro_value_get_string(&field, &name, &name_size);
  if (avro_value_get_by_name(&avro, "age", &field, nullptr) != 0) {
    fail("no age field");
  }
  avro_value_get_int(&field, &age);
  return User{std::string{name, name_size}, age};
}

int main(int argc, char **argv) {
  Config config(argc > 1 ? argv[1] : "sncloud.ini");
  const auto bootstrap_servers = config.bootstrap_servers();
  const auto topic = config.topic();
  auto schema_registry_url = config.schema_registry_url();
  const auto token = config.token();
  const auto group_id = config.group_id();

  // libserdes uses the "<scheme>://<username>:<password>@<path>" as the format
  // to configure basic authentication
  auto pos = schema_registry_url.find("://");
  if (pos == std::string::npos) {
    fail(schema_registry_url + " does not contain \"://\"");
  }
  schema_registry_url = schema_registry_url.substr(0, pos) + "://user:" + token + "@" +
                        schema_registry_url.substr(pos + 3);

  // Initialize the serdes object
  char errstr[512];
  auto *sconf = serdes_conf_new(errstr, sizeof(errstr), "schema.registry.url",
                                schema_registry_url.c_str(), nullptr);
  if (sconf == nullptr) {
    fail("Failed to create serdes config: " + std::string(errstr));
  }
  auto serdes = serdes_new(sconf, errstr, sizeof(errstr));
  if (!serdes) {
    fail("Failed to create serdes: " + std::string(errstr));
  }
  std::unique_ptr<serdes_t, decltype(&serdes_destroy)> serdes_guard{serdes, &serdes_destroy};

  auto rk_conf = rd_kafka_conf_new();
  auto configure = [rk_conf, &errstr](const std::string &key, const std::string &value) {
    if (rd_kafka_conf_set(rk_conf, key.c_str(), value.c_str(), errstr, sizeof(errstr)) !=
        RD_KAFKA_CONF_OK) {
      fail("Failed to set " + key + " => " + value + ": " + errstr);
    }
  };

  configure("bootstrap.servers", bootstrap_servers);
  configure("group.id", group_id);
  configure("auto.offset.reset", "earliest");
  configure("enable.partition.eof", "true");
  configure("sasl.mechanism", "PLAIN");
  configure("security.protocol", "SASL_SSL");
  configure("sasl.username", "user");
  configure("sasl.password", "token:" + token);

  fprintf(stderr, "%% Starting consumer for topic %s with group.id %s\n", topic.c_str(),
          group_id.c_str());
  auto rk = rd_kafka_new(RD_KAFKA_CONSUMER, rk_conf, errstr, sizeof(errstr));
  if (!rk) {
    fail("Failed to create consumer: " + std::string(errstr));
  }
  std::unique_ptr<rd_kafka_t, decltype(&rd_kafka_destroy)> rk_guard{rk, &rd_kafka_destroy};

  auto topics = rd_kafka_topic_partition_list_new(1);
  rd_kafka_topic_partition_list_add(topics, topic.c_str(), RD_KAFKA_PARTITION_UA);
  if (rd_kafka_subscribe(rk, topics) != RD_KAFKA_RESP_ERR_NO_ERROR) {
    fail("Failed to subscribe to topic: " + std::string(rd_kafka_err2str(rd_kafka_last_error())));
  }
  fprintf(stderr, "%% Waiting for User message...\n");

  constexpr int max_attempts = 50;
  int attempts = 0;
  while (attempts < max_attempts) {
    auto msg = rd_kafka_consumer_poll(rk, 1000);
    if (msg == nullptr) {
      attempts++;
      continue;
    }
    attempts = 0;
    std::unique_ptr<rd_kafka_message_t, decltype(&rd_kafka_message_destroy)> msg_guard{
        msg, &rd_kafka_message_destroy};
    if (msg->err == RD_KAFKA_RESP_ERR__PARTITION_EOF) {
      fprintf(stderr, "%% Reached end of partition\n");
      break;
    }
    if (msg->err) {
      fail("Failed to poll: " + std::string(rd_kafka_message_errstr(msg)));
    }

    fprintf(stderr, "%% Received message (size: %zd) deserialize partition %d offset %lld\n",
            msg->len, msg->partition, msg->offset);
    try {
      auto user = deserialize(serdes, msg->payload, msg->len);
      fprintf(stderr, "%% Received User message - Name: %s, Age: %d\n", user.name().c_str(),
              user.age());
    } catch (const std::runtime_error &e) {
      fprintf(stderr, "%% Received non-user message: %s\n", e.what());
    }
  }

  return 0;
}
