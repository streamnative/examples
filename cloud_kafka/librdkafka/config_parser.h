#ifndef CONFIG_PARSER_H
#define CONFIG_PARSER_H

#include <fstream>
#include <map>
#include <string>

class ConfigParser {
private:
    std::map<std::string, std::map<std::string, std::string> > sections;

public:
    bool parse(const std::string& filename) {
        std::ifstream file(filename);
        if (!file.is_open()) {
            return false;
        }

        std::string line;
        std::string current_section;

        while (std::getline(file, line)) {
            // Trim whitespace
            line.erase(0, line.find_first_not_of(" \t"));
            line.erase(line.find_last_not_of(" \t") + 1);

            // Skip empty lines and comments
            if (line.empty() || line[0] == '#') {
                continue;
            }

            // Section header
            if (line[0] == '[' && line[line.length() - 1] == ']') {
                current_section = line.substr(1, line.length() - 2);
                continue;
            }

            // Key-value pair
            size_t delimiter_pos = line.find('=');
            if (delimiter_pos != std::string::npos && !current_section.empty()) {
                std::string key = line.substr(0, delimiter_pos);
                std::string value = line.substr(delimiter_pos + 1);
                
                // Trim key and value
                key.erase(0, key.find_first_not_of(" \t"));
                key.erase(key.find_last_not_of(" \t") + 1);
                value.erase(0, value.find_first_not_of(" \t"));
                value.erase(value.find_last_not_of(" \t") + 1);

                sections[current_section][key] = value;
            }
        }

        return true;
    }

    std::string get(const std::string& section, const std::string& key, const std::string& default_value = "") {
        if (sections.count(section) && sections[section].count(key)) {
            return sections[section][key];
        }
        return default_value;
    }
};

#endif // CONFIG_PARSER_H