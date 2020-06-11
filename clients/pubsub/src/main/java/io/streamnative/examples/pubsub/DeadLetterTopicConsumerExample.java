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
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamnative.examples.pubsub;

import io.streamnative.examples.common.ConsumerFlags;
import io.streamnative.examples.common.ExampleRunner;

import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

/**
 * Example that demonstrates how to use dead letter topic.
 **/
public class DeadLetterTopicConsumerExample extends ExampleRunner<ConsumerFlags> {
    @Override
    protected String name() {
        return DeadLetterTopicConsumerExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "Example that demonstrates how to use dead letter topic.";
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
                    .ackTimeout(3, TimeUnit.SECONDS)
                    .deadLetterPolicy(DeadLetterPolicy.builder()
                            .maxRedeliverCount(3)
                            .build())
                    .subscribe()
            ) {
                // consume numMessages messages,
                // for even numbers, process the message and ack it
                // for odd numbers, skip ack
                // when the number of cycles reached redeliverCount times,
                // DLT will be triggered.
                while (flags.numMessages <= 0 || numReceived < flags.numMessages) {
                    Message<String> msg = consumer.receive();
                    if (Integer.valueOf(msg.getValue().split("-")[1]) % 2 == 0) {
                        consumer.acknowledge(msg);
                        System.out.println("Consumer Received message : " + msg.getValue()
                                + "; Send ack");
                    } else {
                        System.out.println("Consumer Received message : " + msg.getValue()
                                + "; Don't send ack");
                    }
                    ++numReceived;
                }
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
        DeadLetterTopicConsumerExample example = new DeadLetterTopicConsumerExample();
        example.run(args);
    }
}
