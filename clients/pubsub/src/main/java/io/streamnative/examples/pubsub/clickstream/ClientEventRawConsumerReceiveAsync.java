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
package io.streamnative.examples.pubsub.clickstream;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.common.util.ObjectMapperFactory;

/**
 * Consumer to receive events.
 */
public class ClientEventRawConsumerReceiveAsync {

    public static void main(String[] args) throws Exception {

        final String brokerServiceUrl = "pulsar://localhost:6650/";
        final String topic = "clickstream-raw";
        final String sub = "sub-receive-async";

        // Build the client
        PulsarClient client = PulsarClient.builder()
            .serviceUrl(brokerServiceUrl)
            .build();

        try {

            Consumer<byte[]> consumer = client.newConsumer()
                .topic(topic)
                .subscriptionName(sub)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

            CountDownLatch receiveLatch = new CountDownLatch(10);
            AtomicReference<Throwable> errorHolder = new AtomicReference<>();

            try {
                receiveOneMessage(consumer, receiveLatch, errorHolder);
                receiveLatch.await();
            } finally {
                consumer.close();
            }

        } finally {
            client.close();
        }

    }

    private static void receiveOneMessage(Consumer<byte[]> consumer,
                                          CountDownLatch receiveLatch,
                                          AtomicReference<Throwable> errorHolder) {
        consumer.receiveAsync().whenComplete((msg, cause) -> {
            if (null == cause) {
                byte[] data = msg.getValue();
                ClientEvent event;
                try {
                    event = ObjectMapperFactory.getThreadLocal()
                        .readValue(data, ClientEvent.class);
                    System.out.println("Receive event " + msg.getMessageId() + " : " + event);

                    consumer.acknowledgeAsync(msg);

                    receiveLatch.countDown();
                    if (receiveLatch.getCount() > 0) {
                        receiveOneMessage(consumer, receiveLatch, errorHolder);
                    }
                } catch (IOException e) {
                    errorHolder.set(e);
                }
            } else {
                errorHolder.set(cause);
            }
        });
    }

}
