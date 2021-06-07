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
package io.streamnative.examples.kafka;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

/**
 * A token authentication example of Kafka consumer.
 */
public class TokenConsumer {
    public static void main(String[] args) throws IOException {
        // 1. Get the configured parameters from token.properties
        final Properties tokenProps = new Properties();
        tokenProps.load(TokenProducer.class.getClassLoader().getResourceAsStream("token.properties"));
        final String bootstrapServers = tokenProps.getProperty("bootstrap.servers");
        final String topic = tokenProps.getProperty("topic");
        final String group = tokenProps.getProperty("group");
        final String namespace = tokenProps.getProperty("namespace");
        final String token = tokenProps.getProperty("token");

        // 2. Create a consumer with token authentication, which is equivalent to SASL/PLAIN mechanism in Kafka
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                namespace, token));
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(topic));

        // 3. Consume some messages and quit immediately
        boolean running = true;
        while (running) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            if (!records.isEmpty()) {
                records.forEach(record -> System.out.println("Receive record: " + record));
                running = false;
            }
        }
        consumer.close();
    }
}
