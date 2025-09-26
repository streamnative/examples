/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamnative.examples.exschema.json;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializerConfig;

import java.util.HashMap;
import java.util.Map;

public class Configurations {

    protected static final String TOKEN = "<YOUR-TOKEN>";
    protected static final String PULSAR_SERVICE_URL = "<PULSAR-SERVICE-URL>";
    private static final String SCHEMA_REGISTRY_URL = "<SCHEMA-REGISTRY-URL>";

    public static Map<String, Object> getSchemaRegistryConfigs() {
        var map = new HashMap<String, Object>();
        map.put(KafkaJsonSchemaSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
        map.put(KafkaJsonSchemaSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        map.put(
                KafkaJsonSchemaSerializerConfig.USER_INFO_CONFIG,
                String.format("%s:%s", "public", TOKEN));
        return map;
    }

}
