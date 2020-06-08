package io.streamnative.examples.pubsub.clickstream;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.common.util.ObjectMapperFactory;

/**
 * Example that demonstrates an producer that publish messages using a raw producer.
 */
public class ClientEventRawProducerAsync {

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

                    // send the message asynchronously
                    producer.newMessage()
                        .key(String.valueOf(event.userid))
                        .value(data)
                        .sendAsync()
                        .whenComplete((msgId, cause) -> {
                            if (null == cause) {
                                System.out.println("Successfully published event @ " + msgId);
                            } else {
                                System.err.println("Failed to publish event : ");
                                cause.printStackTrace();
                            }
                        });


                }

                // flush all outstanding requests
                producer.flush();

            } finally {
                producer.close();
            }


        } finally {
            client.close();
        }

    }

}
