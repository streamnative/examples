#pragma once

#include <stdexcept>
#include <string>

// The example uses the Avro C library for serialization and deserialization
class User {
 public:
  User(const std::string &name, int age) : name_(name), age_(age) {}

  const std::string &name() const noexcept { return name_; }
  int age() const noexcept { return age_; }

 private:
  const std::string name_;
  const int age_;
};

inline const std::string &user_schema_def() {
  static std::string schema = R"({
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
  return schema;
}

inline void fail(const std::string &msg) { throw std::runtime_error(msg); }
