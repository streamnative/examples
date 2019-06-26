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

import io.streamnative.examples.common.ConsumerFlags;
import io.streamnative.examples.common.ExampleRunner;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

/**
 * Example that demonstrates an consumer that consume messages using
 * synchronous {@link Consumer#receive()} method.
 */
public class SyncStringConsumerExample extends ExampleRunner<ConsumerFlags> {

    @Override
    protected String name() {
        return SyncStringConsumerExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "An example demonstrates consuming messages using synchronous receive method";
    }

    @Override
    protected ConsumerFlags flags() {
        return new ConsumerFlags();
    }

    @Override
    protected void run(ConsumerFlags flags) throws Exception {

        try (PulsarClient client = PulsarClient.builder()
             .serviceUrl(flags.binaryServiceUrl)
             .build()) {

            int numReceived = 0;
            try (Consumer<String> consumer = client.newConsumer(Schema.STRING)
                 .topic(flags.topic)
                 .subscriptionName(flags.subscriptionName)
                 .subscriptionType(flags.subscriptionType)
                 .subscriptionInitialPosition(flags.subscriptionInitialPosition)
                 .subscribe()) {


                while ((flags.numMessages > 0 && numReceived < flags.numMessages) || flags.numMessages <= 0) {
                    Message<String> msg = consumer.receive();
                    System.out.println("Received message : value = '" + msg.getValue()
                        + "', sequence = " + msg.getSequenceId());

                    ++numReceived;
                }
                System.out.println("Successfully received " + numReceived + " messages");
            } catch (PulsarClientException ie) {
                if (ie.getCause() instanceof InterruptedException) {
                    System.out.println("Successfully received " + numReceived + " messages");
                    Thread.currentThread().interrupt();
                } else {
                    throw ie;
                }
            }
        }

    }

    public static void main(String[] args) {
        SyncStringConsumerExample example = new SyncStringConsumerExample();
        example.run(args);
    }

}
