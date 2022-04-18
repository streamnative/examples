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

import io.streamnative.pulsar.handlers.kop.security.oauth.OauthLoginCallbackHandler;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OAuthConsumer {

    public static void main(String[] args) throws IOException {
        // 1. Get the configured parameters from oauth.properties
        final Properties properties = new Properties();
        properties.load(TokenProducer.class.getClassLoader().getResourceAsStream("oauth.properties"));
        String bootstrapServers = properties.getProperty("bootstrap.servers");
        String topic = properties.getProperty("topic");
        String group = properties.getProperty("group");
        String issuerUrl = properties.getProperty("issuerUrl");
        String credentialsUrl = properties.getProperty("credentialsUrl");
        String audience = properties.getProperty("audience");

        // 2. Create a consumer with OAuth authentication.
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty("sasl.login.callback.handler.class", OauthLoginCallbackHandler.class.getName());
        props.setProperty("security.protocol", "SASL_PLAINTEXT");
        props.setProperty("sasl.mechanism", "OAUTHBEARER");
        final String jaasTemplate = "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required"
                + " oauth.issuer.url=\"%s\""
                + " oauth.credentials.url=\"%s\""
                + " oauth.audience=\"%s\";";
        props.setProperty("sasl.jaas.config", String.format(jaasTemplate,
                issuerUrl,
                "file://" + Paths.get(credentialsUrl).toAbsolutePath(),
                audience
        ));
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(topic));

        // 3. Consume some messages and quit immediately
        boolean running = true;
        while (running) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            if (!records.isEmpty()) {
                records.forEach(record -> System.out.println("Receive record: " + record.value() + " from "
                        + record.topic() + "-" + record.partition() + "@" + record.offset()));
                running = false;
            }
        }
        consumer.close();
    }
}
