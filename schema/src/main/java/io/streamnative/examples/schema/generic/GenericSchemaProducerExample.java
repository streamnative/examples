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
package io.streamnative.examples.schema.generic;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.schema.GenericRecord;
import org.apache.pulsar.client.api.schema.GenericSchema;
import org.apache.pulsar.client.api.schema.RecordSchemaBuilder;
import org.apache.pulsar.client.api.schema.SchemaBuilder;
import org.apache.pulsar.common.schema.SchemaInfo;
import org.apache.pulsar.common.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example that demonstrates a producer producing messages using
 * {@link org.apache.pulsar.client.api.schema.GenericSchema}.
 */
public class GenericSchemaProducerExample {

    private static final Logger log = LoggerFactory.getLogger(GenericSchemaProducerExample.class);

    private static final String TOPIC = "avro-payments";

    public static void main(final String[] args) {

        final String pulsarServiceUrl = "pulsar://localhost:6650";

        try (PulsarClient client = PulsarClient.builder()
             .serviceUrl(pulsarServiceUrl)
             .build()) {

            RecordSchemaBuilder schemaBuilder = SchemaBuilder.record(
                "io.streamnative.examples.schema.avro"
            );
            schemaBuilder.field("id")
                .type(SchemaType.STRING)
                .required();
            schemaBuilder.field("amount")
                .type(SchemaType.DOUBLE)
                .required();
            SchemaInfo schemaInfo = schemaBuilder.build(SchemaType.AVRO);
            GenericSchema<GenericRecord> schema = Schema.generic(schemaInfo);

            try (Producer<GenericRecord> producer = client.newProducer(schema)
                 .topic(TOPIC)
                 .create()) {

                final int numMessages = 10;

                for (long i = 0; i < numMessages; i++) {
                    final String orderId = "id-" + i;
                    final double amount = 1000.00d * i;

                    GenericRecord record = schema.newRecordBuilder()
                        .set("id", orderId)
                        .set("amount", amount)
                        .build();

                    // send the payment in an async way
                    producer.newMessage()
                        .key(orderId)
                        .value(record)
                        .sendAsync();
                }
                // flush out all outstanding messages
                producer.flush();

                System.out.printf("Successfully produced %d messages to a topic called %s%n",
                    numMessages, TOPIC);

            }
        } catch (PulsarClientException e) {
            System.err.println("Failed to produce generic avro messages to pulsar:");
            e.printStackTrace();
            Runtime.getRuntime().exit(-1);
        }
    }

}
