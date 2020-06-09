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

import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;

/**
 * Consumer to receive events.
 */
public class ClientEventAvroConsumer {

    public static void main(String[] args) throws Exception {

        final String brokerServiceUrl = "pulsar://localhost:6650/";
        final String topic = "clickstream-avro";
        final String sub = "sub-receive";

        // Build the client
        PulsarClient client = PulsarClient.builder()
            .serviceUrl(brokerServiceUrl)
            .build();

        try {

            Consumer<ClientEvent> consumer = client.newConsumer(Schema.AVRO(ClientEvent.class))
                .topic(topic)
                .subscriptionName(sub)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

            try {

                Message<ClientEvent> msg = consumer.receive(500, TimeUnit.MILLISECONDS);
                while (null != msg) {
                    ClientEvent event = msg.getValue();
                    System.out.println("Receive event " + msg.getMessageId() + " : " + event);

                    consumer.acknowledgeAsync(msg);
                }

            } finally {
                consumer.close();
            }


        } finally {
            client.close();
        }

    }

}
