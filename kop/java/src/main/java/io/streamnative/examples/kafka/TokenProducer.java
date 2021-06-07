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

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * A token authentication example of Kafka producer.
 */
public class TokenProducer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final String bootstrapServers = "localhost:9092";
        final String topic = "persistent://public/default/my-topic";
        final String namespace = "public/default";
        final String token = "token:eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJyb290In0.gt-c0gy2JL6LyXziRfW3DbiTxsaWDaQEFuWRiYkn2Q"
                + "t5mmbzHWPZw2LI06YcK3n2ekq00MB5VlOiZRpyJPw61pn6uE6bS1e5pOSN5XqWZT9yoLd5tviDdyTG4itcIhiRThMZBYuajGuSRf"
                + "g0zDN3onxWedvgEkA_3krXxKF6RyWJM3JFtfh6XxkmRF3djVswpjQMJIqB9td42_noVoHkVdcXO_Gev7_X9tcdetM-bIvpvpbOFf"
                + "nQoMhZ1_kf3vbb0fhHp9oixpO0bk-TExakGBSp4wTqys2PcKN9e9B8Yz4x-4fyYBWi0oTURzE-RDAPjR6OcXRICe4QxIsKfnS_5g"
                ;

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                namespace, token));

        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        final Future<RecordMetadata> recordMetadataFuture = producer.send(new ProducerRecord<>(topic, "hello"));
        final RecordMetadata recordMetadata = recordMetadataFuture.get();
        System.out.println("Send hello to " + recordMetadata);
        producer.close();
    }
}
