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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OAuthProducer {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // 1. Get the configured parameters from oauth.properties
        final Properties properties = new Properties();
        properties.load(OAuthProducer.class.getClassLoader().getResourceAsStream("oauth.properties"));
        String bootstrapServers = properties.getProperty("bootstrap.servers");
        String topic = properties.getProperty("topic");
        String issuerUrl = properties.getProperty("issuerUrl");
        String credentialsUrl = properties.getProperty("credentialsUrl");
        String audience = properties.getProperty("audience");

        // 2. Create a producer with OAuth authentication.
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
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
        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 3. Produce one message
        final Future<RecordMetadata> recordMetadataFuture = producer.send(new ProducerRecord<>(topic, "hello"));
        final RecordMetadata recordMetadata = recordMetadataFuture.get();
        System.out.println("Send hello to " + recordMetadata);
        producer.close();
    }
}
