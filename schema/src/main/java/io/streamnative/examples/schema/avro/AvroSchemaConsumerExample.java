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
package io.streamnative.examples.schema.avro;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example that demonstrates a consumer consuming messages using {@link Schema#AVRO(Class)}.
 */
public class AvroSchemaConsumerExample {

    private static final Logger log = LoggerFactory.getLogger(AvroSchemaProducerExample.class);

    private static final String TOPIC = "avro-payments";

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(final String[] args) {

        final String pulsarServiceUrl = "pulsar://localhost:6650";

        try (PulsarClient client = PulsarClient.builder()
             .serviceUrl(pulsarServiceUrl)
             .build()) {

            Schema<Payment> paymentSchema = Schema.AVRO(Payment.class);

            try (Consumer<Payment> consumer = client.newConsumer(paymentSchema)
                 .topic(TOPIC)
                 .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                 .subscriptionName("test-payments")
                 .subscribe()) {

                final int numMessages = 10;

                while (true) {
                    Message<Payment> msg = consumer.receive();

                    final String key = msg.getKey();
                    final Payment payment = msg.getValue();

                    System.out.printf("key = %s, value = %s%n", key, payment);
                }
            }
        } catch (PulsarClientException e) {
            log.error("Failed to consume avro messages from pulsar", e);
            Runtime.getRuntime().exit(-1);
        }
    }

}
