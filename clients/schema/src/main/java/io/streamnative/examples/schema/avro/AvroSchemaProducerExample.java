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

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.schema.SchemaDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example that demonstrates a producer producing messages using
 * {@link org.apache.pulsar.client.api.Schema#AVRO(Class)}.
 */
public class AvroSchemaProducerExample {

    private static final Logger log = LoggerFactory.getLogger(AvroSchemaProducerExample.class);

    private static final String TOPIC = "avro-payments";

    public static void main(final String[] args) {

        final String pulsarServiceUrl = "pulsar://localhost:6650";

        try (PulsarClient client = PulsarClient.builder()
             .serviceUrl(pulsarServiceUrl)
             .build()) {

            Schema<Payment> paymentSchema = Schema.AVRO(
                SchemaDefinition.<Payment>builder()
                    .withPojo(Payment.class)
                    .withAlwaysAllowNull(false)
                    .build()
            );

            try (Producer<Payment> producer = client.newProducer(paymentSchema)
                 .topic(TOPIC)
                 .create()) {

                final int numMessages = 10;

                for (long i = 0; i < numMessages; i++) {
                    final String orderId = "id-" + i;
                    final Payment payment = new Payment(orderId, 1000.00d * i);
                    // send the payment in an async way
                    producer.newMessage()
                        .key(orderId)
                        .value(payment)
                        .sendAsync();
                }
                // flush out all outstanding messages
                producer.flush();

                System.out.printf("Successfully produced %d messages to a topic called %s%n",
                    numMessages, TOPIC);

            }
        } catch (PulsarClientException e) {
            log.error("Failed to produce avro messages to pulsar", e);
            Runtime.getRuntime().exit(-1);
        }
    }

}
