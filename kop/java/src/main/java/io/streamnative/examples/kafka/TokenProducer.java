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
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        // 1. Get the configured parameters from token.properties
        final Properties tokenProps = new Properties();
        tokenProps.load(TokenProducer.class.getClassLoader().getResourceAsStream("token.properties"));
        final String bootstrapServers = tokenProps.getProperty("bootstrap.servers");
        final String topic = tokenProps.getProperty("topic");
        final String namespace = tokenProps.getProperty("namespace");
        final String token = tokenProps.getProperty("token");

        // 2. Create a producer with token authentication, which is equivalent to SASL/PLAIN mechanism in Kafka
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

        // 3. Produce one message
        final Future<RecordMetadata> recordMetadataFuture = producer.send(new ProducerRecord<>(topic, "hello"));
        final RecordMetadata recordMetadata = recordMetadataFuture.get();
        System.out.println("Send hello to " + recordMetadata);
        producer.close();
    }
}
