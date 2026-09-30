package io.streamnative.examples.avro;

import java.util.HashMap;
import java.util.Map;

/** Shared environment configuration for the Pulsar producer and consumer. */
final class DemoConfig {
    private DemoConfig() {}

    static String serviceUrl() {
        return required("PULSAR_SERVICE_URL");
    }

    static String token() {
        return required("PULSAR_TOKEN");
    }

    static Map<String, Object> registryConfigs() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("schema.registry.url", required("SCHEMA_REGISTRY_URL"));
        configs.put("basic.auth.credentials.source", "USER_INFO");
        configs.put("basic.auth.user.info", "public:token:" + token());
        return configs;
    }

    static String topic() {
        return optional("PULSAR_TOPIC", "test-pulsar-external-avro-reference");
    }

    static int messageCount() {
        String value = optional("MESSAGE_COUNT", "10");
        try {
            int count = Integer.parseInt(value);
            if (count > 0) {
                return count;
            }
        } catch (NumberFormatException ignored) {
            // Report the same validation error for malformed and out-of-range values.
        }
        throw new IllegalArgumentException("MESSAGE_COUNT must be a positive integer");
    }

    static String subscription() {
        return optional("PULSAR_SUBSCRIPTION", "pulsar-avro-reference-sub");
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Set environment variable " + name);
        }
        return value;
    }

    private static String optional(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
