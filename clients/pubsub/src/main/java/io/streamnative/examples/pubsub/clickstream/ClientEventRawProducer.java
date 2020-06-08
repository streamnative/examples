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

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.common.util.ObjectMapperFactory;

/**
 * Example that demonstrates an producer that publish messages using a raw producer.
 */
public class ClientEventRawProducer {

    public static void main(String[] args) throws Exception {

        final String brokerServiceUrl = "pulsar://localhost:6650/";
        final String topic = "clickstream-raw";
        final int numMessages = 10;

        // Build the client
        PulsarClient client = PulsarClient.builder()
            .serviceUrl(brokerServiceUrl)
            .build();

        try {

            Producer<byte[]> producer = client.newProducer()
                .enableBatching(false)
                .topic(topic)
                .create();

            try {

                for (int i = 0; i < numMessages; i++) {

                    ClientEvent event = new ClientEvent();
                    event.setTimestamp(System.currentTimeMillis());
                    event.setIp("127.0.0." + i);
                    event.setRequest("POST");
                    event.setStatus(200);
                    event.setUserid(10000 + i);
                    event.setBytes(1024 * i);
                    event.setAgent("orm-examples");

                    // serialize the event
                    byte[] data = ObjectMapperFactory.getThreadLocal().writeValueAsBytes(event);

                    // send the message
                    MessageId msgId = producer.newMessage()
                        .key(String.valueOf(event.userid))
                        .value(data)
                        .send();

                    System.out.println("Successfully published event @ " + msgId);

                }

            } finally {
                producer.close();
            }


        } finally {
            client.close();
        }

    }

}
