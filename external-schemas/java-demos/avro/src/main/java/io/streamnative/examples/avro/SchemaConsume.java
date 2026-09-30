/**
 * Copyright StreamNative, Inc. and contributors
 *
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
package io.streamnative.examples.avro;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.serializers.schema.id.SchemaId;
import io.streamnative.schemas.external.KafkaSchemaFactory;
import io.streamnative.schemas.external.test.avro.Member;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;

public class SchemaConsume {

    public static void main(String[] args) throws Exception {
        int count = DemoConfig.messageCount();
        consume(DemoConfig.serviceUrl(), DemoConfig.registryConfigs(), DemoConfig.token(),
                DemoConfig.topic(), DemoConfig.subscription(), count);
    }

    static void consume(
            String serviceUrl,
            Map<String, Object> registryConfigs,
            String jwtToken,
            String topic,
            String subscription,
            int count)
            throws Exception {
        var consumerConfigs = new HashMap<>(registryConfigs);
        consumerConfigs.put("auto.register.schemas", false);
        // The EXTERNAL schema adapter resolves the Registry ID carried in Pulsar message metadata.
        var externalSchema = new KafkaSchemaFactory(consumerConfigs).avro(Member.class);
        var clientBuilder = PulsarClient.builder().serviceUrl(serviceUrl);
        if (jwtToken != null && !jwtToken.isBlank()) {
            clientBuilder.authentication(AuthenticationFactory.token(jwtToken));
        }
        try (PulsarClient client = clientBuilder.build();
                Consumer<Member> consumer =
                        client.newConsumer(externalSchema)
                                .topic(topic)
                                .subscriptionName(subscription)
                                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                                .subscribe()) {
            for (int received = 0; received < count; received++) {
                var message = consumer.receive(60, TimeUnit.SECONDS);
                if (message == null) {
                    throw new IllegalStateException(String.format(
                            "Timed out after 60 seconds waiting for the next message on %s; received %d/%d",
                            topic, received, count));
                }
                var schemaId = new SchemaId(AvroSchema.TYPE);
                schemaId.fromBytes(ByteBuffer.wrap(message.getSchemaId().orElseThrow()));
                Member member = message.getValue();
                if (member == null || member.getAddress() == null) {
                    throw new IllegalStateException("Expected Member with a referenced Address record");
                }
                System.out.printf(
                        "Received %d/%d: Member{name=%s, address={city=%s, zip=%s}} using external schema ID %d from %s, messageId=%s%n",
                        received + 1, count, member.getName(), member.getAddress().getCity(),
                        member.getAddress().getZip(), schemaId.getId(), message.getTopicName(), message.getMessageId());
                consumer.acknowledge(message);
            }
            System.out.printf("Finished receiving and acknowledging %d messages.%n", count);
        }
    }
}
