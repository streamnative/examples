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
package io.streamnative.examples.schema.kv;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.common.schema.KeyValue;
import org.apache.pulsar.common.schema.KeyValueEncodingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example that demonstrates a consumer consuming key/value messages using
 * {@link Schema#KeyValue(Class, Class)}.
 */
public class KeyValueSeparatedSchemaConsumerExample {

    private static final Logger log = LoggerFactory.getLogger(KeyValueSeparatedSchemaConsumerExample.class);

    private static final String TOPIC = "keyvalue-separated-topic";

    public static void main(final String[] args) {

        final String pulsarServiceUrl = "pulsar://localhost:6650";

        try (PulsarClient client = PulsarClient.builder()
             .serviceUrl(pulsarServiceUrl)
             .build()) {

            Schema<KeyValue<Integer, String>> kvSchema = Schema.KeyValue(
                Schema.INT32,
                Schema.STRING,
                KeyValueEncodingType.SEPARATED
            );

            try (Consumer<KeyValue<Integer, String>> consumer = client.newConsumer(kvSchema)
                 .topic(TOPIC)
                 .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                 .subscriptionName("test-kv-separated")
                 .subscribe()) {

                while (true) {
                    Message<KeyValue<Integer, String>> msg = consumer.receive();
                    KeyValue<Integer, String> kv = msg.getValue();

                    System.out.printf("key = %s, value = %s%n", kv.getKey(), kv.getValue());
                }
            }
        } catch (PulsarClientException e) {
            log.error("Failed to consume key/value messages from pulsar", e);
            Runtime.getRuntime().exit(-1);
        }

    }

}
