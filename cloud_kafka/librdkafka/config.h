#pragma once

#include <stdexcept>

#include "SimpleIni.h"

class Config {
 public:
  Config(const std::string& filename) {
    auto rc = ini_.LoadFile(filename.c_str());
    if (rc < 0) {
      throw std::runtime_error("Failed to load config file: " + filename);
    }
  }

  std::string bootstrap_servers() const {
    return get_non_empty_string("common", "bootstrap.servers");
  }

  std::string topic() const { return get_non_empty_string("common", "topic"); }

  std::string token() const { return get_non_empty_string("common", "token"); }

  std::string schema_registry_url() const {
    return get_non_empty_string("schema.registry", "url");
  }

  std::string group_id() const {
    return ini_.GetValue("consumer", "group.id", "default-group");
  }

 private:
  CSimpleIniA ini_;

  std::string get_non_empty_string(const std::string& section,
                                   const std::string& name) const {
    std::string result = ini_.GetValue(section.c_str(), name.c_str(), "");
    if (result.empty()) {
      throw std::runtime_error("empty " + name + " in [" + section +
                               "] section");
    }
    return result;
  }
};
