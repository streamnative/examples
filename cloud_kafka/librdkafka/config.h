#pragma once

#include <INIReader.h>

#include <stdexcept>

class Config {
 public:
  Config(const std::string& filename) : reader_(filename) {}

  std::string bootstrap_servers() const {
    return get_non_empty_string("common", "bootstrap.servers");
  }

  std::string topic() const { return get_non_empty_string("common", "topic"); }

  std::string token() const { return reader_.GetString("common", "token", ""); }

  std::string schema_registry_url() const {
    return get_non_empty_string("schema.registry", "url");
  }

  std::string group_id() const {
    return reader_.GetString("consumer", "group.id", "default-group");
  }

 private:
  INIReader reader_;

  std::string get_non_empty_string(const std::string& section,
                                   const std::string& name) const {
    auto result = reader_.GetString(section, name, "");
    if (result.empty()) {
      throw std::runtime_error("empty " + name + " in [" + section +
                               "] section");
    }
    return result;
  }
};
