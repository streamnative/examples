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
    public static void main(String[] args) {
        final String bootstrapServers = "localhost:9092";
        final String topic = "persistent://public/default/my-topic";
        final String group = "my-group";
        final String namespace = "public/default";
        final String token = "token:eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJyb290In0.gt-c0gy2JL6LyXziRfW3DbiTxsaWDaQEFuWRiYkn2Q"
                + "t5mmbzHWPZw2LI06YcK3n2ekq00MB5VlOiZRpyJPw61pn6uE6bS1e5pOSN5XqWZT9yoLd5tviDdyTG4itcIhiRThMZBYuajGuSRf"
                + "g0zDN3onxWedvgEkA_3krXxKF6RyWJM3JFtfh6XxkmRF3djVswpjQMJIqB9td42_noVoHkVdcXO_Gev7_X9tcdetM-bIvpvpbOFf"
                + "nQoMhZ1_kf3vbb0fhHp9oixpO0bk-TExakGBSp4wTqys2PcKN9e9B8Yz4x-4fyYBWi0oTURzE-RDAPjR6OcXRICe4QxIsKfnS_5g"
                ;

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
