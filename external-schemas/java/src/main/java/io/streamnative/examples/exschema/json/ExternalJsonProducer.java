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

import io.streamnative.schemas.external.KafkaSchemaFactory;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;

import static io.streamnative.examples.exschema.json.Configurations.PULSAR_SERVICE_URL;
import static io.streamnative.examples.exschema.json.Configurations.TOKEN;
import static io.streamnative.examples.exschema.json.Configurations.getSchemaRegistryConfigs;

@Slf4j
public class ExternalJsonProducer {

    public void produce() throws Exception {
        String topic = "testExternalJsonSchema";

        KafkaSchemaFactory kafkaSchemaFactory = new KafkaSchemaFactory(getSchemaRegistryConfigs());
        Schema<User> schema = kafkaSchemaFactory.json(User.class);

        @Cleanup
        PulsarClient client =
                PulsarClient.builder()
                        .serviceUrl(PULSAR_SERVICE_URL)
                        .authentication(AuthenticationFactory.token(TOKEN))
                        .build();

        @Cleanup Producer<User> producer = client.newProducer(schema).topic(topic).create();

        for (int i = 0; i < 10; i++) {
            producer.send(new User("name-" + i, 10 + i));
        }
    }

    public static void main(String[] args) {
        try {
            new ExternalJsonProducer().produce();
        } catch (Exception e) {
            log.error("Failed to produce messages", e);
            throw new RuntimeException(e);
        }
    }

}
