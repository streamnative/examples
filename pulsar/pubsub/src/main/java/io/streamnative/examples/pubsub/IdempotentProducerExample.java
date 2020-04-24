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
package io.streamnative.examples.pubsub;

import io.streamnative.examples.common.ExampleRunner;
import io.streamnative.examples.common.ProducerFlags;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;

/**
 * Example that demonstrates idempotent producers producing messages exactly-once.
 */
public class IdempotentProducerExample extends ExampleRunner<ProducerFlags> {

    @Override
    protected String name() {
        return IdempotentProducerExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "An example demonstrates how to produce messages exactly-once using idempotent producers";
    }

    @Override
    protected ProducerFlags flags() {
        return new ProducerFlags();
    }

    protected void run(ProducerFlags flags) throws Exception {

        try (PulsarClient client = PulsarClient.builder()
             .serviceUrl(flags.binaryServiceUrl)
             .build()) {

            try (Producer<String> producer = client.newProducer(Schema.STRING)
                 .topic(flags.topic)
                 .create()) {

                final int numMessages = Math.max(flags.numMessages, 1);

                // publish the first 10 messages with sequence id [0 - 10)
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                        .value("value-" + i)
                        // the sequence id is used an unique identifier for
                        // de-duplication.
                        .sequenceId(i)
                        .send();
                }

                // try to produce duplicated messages (with same sequence id)
                // to simulate retries after failures
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                        .value("duplicated-value-" + i)
                        .sequenceId(i)
                        .send();
                }

                // try to produce new messages
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                        .value("value-" + (numMessages + i))
                        .sequenceId(numMessages + i)
                        .send();
                }
            }

        }

    }

    public static void main(String[] args) {
        IdempotentProducerExample example = new IdempotentProducerExample();
        example.run(args);
    }

}
