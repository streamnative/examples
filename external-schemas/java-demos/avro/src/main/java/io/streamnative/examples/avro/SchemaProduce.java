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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference;
import io.streamnative.schemas.external.KafkaSchemaFactory;
import io.streamnative.schemas.external.test.avro.Address;
import io.streamnative.schemas.external.test.avro.Member;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

public class SchemaProduce {

    public static void main(String[] args) throws Exception {
        int count = DemoConfig.messageCount();
        produce(DemoConfig.serviceUrl(), DemoConfig.registryConfigs(), DemoConfig.token(),
                DemoConfig.topic(), count);
    }

    static int produce(
            String serviceUrl, Map<String, Object> registryConfigs, String jwtToken, String topic, int count)
            throws Exception {
        String registryUrl = (String) registryConfigs.get("schema.registry.url");
        String addressName = Address.getClassSchema().getFullName();
        String addressSubject = topic + "-address";
        String memberSubject = topic + "-value";
        AvroSchema addressSchema = new AvroSchema(Address.getClassSchema());
        String memberDefinition =
                """
                {
                  "type": "record",
                  "name": "Member",
                  "namespace": "io.streamnative.schemas.external.test.avro",
                  "fields": [
                    {"name": "name", "type": "string"},
                    {"name": "address", "type": "io.streamnative.schemas.external.test.avro.Address"}
                  ]
                }
                """;
        try (var registry = new CachedSchemaRegistryClient(registryUrl, 10, registryConfigs)) {
            int addressId = registry.register(addressSubject, addressSchema);
            int version = registry.getVersion(addressSubject, addressSchema);
            var reference = new SchemaReference(addressName, addressSubject, version);
            var memberSchema =
                    new AvroSchema(
                            memberDefinition,
                            List.of(reference),
                            Map.of(addressName, addressSchema.canonicalString()),
                            null);
            int memberId = registry.register(memberSubject, memberSchema);
            System.out.printf(
                    "Registered Address: subject=%s, id=%d, version=%d%n",
                    addressSubject, addressId, version);
            System.out.printf(
                    "Registered Member: subject=%s, id=%d, references=%s%n",
                    memberSubject, memberId, memberSchema.references());

            var producerConfigs = new HashMap<>(registryConfigs);
            producerConfigs.put("auto.register.schemas", false);
            producerConfigs.put("use.schema.id", memberId);
            producerConfigs.put("id.compatibility.strict", false);
            var externalSchema = new KafkaSchemaFactory(producerConfigs).avro(Member.class);
            var clientBuilder = PulsarClient.builder().serviceUrl(serviceUrl);
            if (jwtToken != null && !jwtToken.isBlank()) {
                // Pulsar token authentication accepts the raw JWT, without the Kafka prefix.
                clientBuilder.authentication(AuthenticationFactory.token(jwtToken));
            }
            var memberVersions = List.copyOf(registry.getAllVersions(memberSubject));
            var addressVersions = List.copyOf(registry.getAllVersions(addressSubject));
            try (PulsarClient client = clientBuilder.build();
                    Producer<Member> producer =
                            client.newProducer(externalSchema).topic(topic).create()) {
                for (int i = 0; i < count; i++) {
                    Member member = new Member("jwt-sr-" + (i + 1), new Address("Shanghai", "200000"));
                    var messageId = producer.send(member);
                    System.out.printf(
                            "Sent %d/%d: %s using external schema ID %d to %s, messageId=%s%n",
                            i + 1, count, member, memberId, producer.getTopic(), messageId);
                }
                System.out.printf("Finished sending %d messages.%n", count);
            }
            if (!registry.getAllVersions(memberSubject).equals(memberVersions)
                    || !registry.getAllVersions(addressSubject).equals(addressVersions)) {
                throw new IllegalStateException("Producing unexpectedly created a schema version");
            }
            return memberId;
        }
    }
}
