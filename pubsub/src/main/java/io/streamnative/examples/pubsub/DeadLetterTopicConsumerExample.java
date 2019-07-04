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
import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

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
                            .deadLetterTopic("persistent://public/deadLetterTopic/deadLetterTopicMessage-d")
                            .build())
                    .subscribe();
                 Consumer<String> deadLetterTopicConsumer = client.newConsumer(Schema.STRING)
                         .topic("persistent://public/deadLetterTopic/deadLetterTopicMessage-d")
                         .subscriptionName(flags.subscriptionName)
                         .subscribe()
            ) {
                // consume message but not acknowledge
                while (flags.numMessages <= 0 || numReceived < flags.numMessages * 4) {
                    Message<String> msg = consumer.receive();
                    System.out.println("Consumer Received message : " + msg.getValue()
                            + " Topic : " + msg.getTopicName());
                    ++numReceived;
                }

                // receive message from dead letter topic
                numReceived = 0;
                while (flags.numMessages <= 0 || numReceived < flags.numMessages) {
                    Message<String> msg = deadLetterTopicConsumer.receive();
                    System.out.println("DeadLetterTopicConsumer Received message : " + msg.getValue()
                            + " Topic : " + msg.getTopicName());
                    consumer.acknowledge(msg);
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
