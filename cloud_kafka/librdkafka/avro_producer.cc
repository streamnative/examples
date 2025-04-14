#include <stdio.h>

#include <memory>
#include <stdexcept>
#include <string>
#include <vector>

#include "config.h"
#include "librdkafka/rdkafka.h"
extern "C" {
#include "libserdes/serdes-avro.h"
}

static void fail(const std::string &msg) { throw std::runtime_error(msg); }

// Use the User schema definition
const std::string user_schema_def = R"({
    "name": "User",
    "type": "record",
    "fields": [
        {
            "name": "name",
            "type": "string"
        },
        {
            "name": "age",
            "type": "int"
        }
    ]
})";

class User {
 public:
  User(const std::string &name, int age) : name_(name), age_(age) {}

  // You must call free() on the return value after it won't be used
  void *serialize(serdes_schema_t *serdes_schema) const {
    auto schema = serdes_schema_avro(serdes_schema);
    auto record_class = avro_generic_class_from_schema(schema);

    avro_value_t record;
    avro_generic_value_new(record_class, &record);

    avro_value_t field;
    if (avro_value_get_by_name(&record, "name", &field, nullptr) == 0) {
      avro_value_set_string(&field, name_.c_str());
    }
    if (avro_value_get_by_name(&record, "age", &field, nullptr) == 0) {
      avro_value_set_int(&field, age_);
    }

    void *ser_buf = nullptr;
    size_t ser_buf_size;
    char errstr[512];
    if (serdes_schema_serialize_avro(serdes_schema, &record, &ser_buf, &ser_buf_size, errstr,
                                     sizeof(errstr))) {
      avro_value_decref(&record);
      fail("serialize_avro() failed: " + std::string(errstr));
    }

    avro_value_decref(&record);
    return ser_buf;
  }

 private:
  const std::string name_;
  const int age_;
};

int main(int argc, char **argv) {
  Config config(argc > 1 ? argv[1] : "sncloud.ini");
  const auto bootstrap_servers = config.bootstrap_servers();
  const auto topic = config.topic();
  auto schema_registry_url = config.schema_registry_url();
  const auto token = config.token();

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

  // Register the value's schema with topic as the subject name
  auto schema_name = topic + "-value";
  auto schema = serdes_schema_add(serdes, schema_name.c_str(), -1, user_schema_def.c_str(),
                                  user_schema_def.length(), errstr, sizeof(errstr));
  if (!schema) {
    fail("Failed to register schema: " + std::string(errstr));
  }
  fprintf(stderr, "%% Added schema %s with id %d\n", serdes_schema_name(schema),
          serdes_schema_id(schema));

  auto rk_conf = rd_kafka_conf_new();
  auto configure = [rk_conf, &errstr](const std::string &key, const std::string &value) {
    if (rd_kafka_conf_set(rk_conf, key.c_str(), value.c_str(), errstr, sizeof(errstr)) !=
        RD_KAFKA_CONF_OK) {
      fail("Failed to set " + key + " => " + value + ": " + errstr);
    }
  };
  configure("bootstrap.servers", bootstrap_servers);
  configure("sasl.mechanism", "PLAIN");
  configure("security.protocol", "SASL_SSL");
  configure("sasl.username", "user");
  configure("sasl.password", "token:" + token);
  int num_messages = 0;
  rd_kafka_conf_set_dr_msg_cb(
      rk_conf, +[](rd_kafka_t *, const rd_kafka_message_t *msg, void *opaque) {
        if (msg->err) {
          fprintf(stderr, "%% Message delivery failed: %s\n", rd_kafka_err2str(msg->err));
        } else {
          fprintf(stderr, "%% Message delivered to partition %d offset %lld\n", msg->partition,
                  msg->offset);
        }
        ++*static_cast<int *>(opaque);
      });
  rd_kafka_conf_set_opaque(rk_conf, &num_messages);

  auto rk = rd_kafka_new(RD_KAFKA_PRODUCER, rk_conf, errstr, sizeof(errstr));
  if (!rk) {
    fail("Failed to create producer: " + std::string(errstr));
  }
  std::unique_ptr<rd_kafka_t, decltype(&rd_kafka_destroy)> rk_guard{rk, &rd_kafka_destroy};

  auto rkt_conf = rd_kafka_topic_conf_new();
  auto rkt = rd_kafka_topic_new(rk, topic.c_str(), rkt_conf);
  std::unique_ptr<rd_kafka_topic_t, decltype(&rd_kafka_topic_destroy)> topic_guard{
      rkt, &rd_kafka_topic_destroy};

  std::vector<User> users{{"Alice", 18}, {"Bob", 19}, {"Charlie", 20}};
  for (auto &&user : users) {
    auto buf = user.serialize(schema);

    if (rd_kafka_produce(rkt, 0, RD_KAFKA_MSG_F_FREE, buf, sizeof(buf), nullptr, 0, nullptr) != 0) {
      free(buf);
      fail("Failed to produce message: " + std::string(rd_kafka_err2str(rd_kafka_last_error())));
    }
  }
  for (int i = 0; i < 50 && num_messages < users.size(); i++) {
    rd_kafka_poll(rk, 100);
  }

  rd_kafka_wait_destroyed(5000);
  return 0;
}
