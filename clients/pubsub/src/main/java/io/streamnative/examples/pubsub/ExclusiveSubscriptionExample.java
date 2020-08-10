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
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;

/**
 * Example that demonstrates how to use exclusive subscription.
 */
public class ExclusiveSubscriptionExample extends ExampleRunner<ConsumerFlags> {

    @Override
    protected String name() {
        return ExclusiveSubscriptionExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "Example that demonstrates how to use exclusive subscription";
    }

    @Override
    protected ConsumerFlags flags() {
        return new ConsumerFlags();
    }

    @Override
    protected void run(ConsumerFlags flags) throws Exception {
        PulsarClient client = PulsarClient.builder()
            .serviceUrl(flags.binaryServiceUrl)
            .build();

        Consumer<String> consumer = client.newConsumer(Schema.STRING)
            .topic(flags.topic)
            .subscriptionName(flags.subscriptionName)
            .subscriptionType(SubscriptionType.Exclusive)
            .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
            .subscribe();

        Producer<String> producer = client.newProducer(Schema.STRING)
            .topic(flags.topic)
            .create();

        for (int i = 0; i < flags.numMessages; i++) {
            producer.send("message-" + i);
        }

        MessageId lastMsgId = null;

        for (int i = 0; i < flags.numMessages; i++) {
            Message<String> msg = consumer.receive();
            System.out.println("Received message : " + msg.getValue());
            lastMsgId = msg.getMessageId();
        }

        consumer.close();

        for (int i = flags.numMessages; i < 2 * flags.numMessages; i++) {
            producer.send("message-" + i);
        }

        consumer = client.newConsumer(Schema.STRING)
            .topic(flags.topic)
            .subscriptionName(flags.subscriptionName)
            .subscriptionType(SubscriptionType.Exclusive)
            .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
            .subscribe();
        consumer.seek(lastMsgId);

        System.out.println("Open a new consumer and seek it to " + lastMsgId);

        for (int i = 0; i < flags.numMessages; i++) {
            Message<String> msg = consumer.receive();
            System.out.println("Received message : " + msg.getValue());
            lastMsgId = msg.getMessageId();
        }

        consumer.close();

        for (int i = 2 * flags.numMessages; i < 3 * flags.numMessages; i++) {
            producer.send("message-" + i);
        }

        consumer = client.newConsumer(Schema.STRING)
            .topic(flags.topic)
            .subscriptionName(flags.subscriptionName)
            .subscriptionType(SubscriptionType.Exclusive)
            .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
            .subscribe();
        consumer.seek(lastMsgId);

        TimeUnit.SECONDS.sleep(2);

        System.out.println("Slept 2 seconds and open a new consumer to seek it to " + lastMsgId);

        for (int i = 0; i < flags.numMessages; i++) {
            Message<String> msg = consumer.receive();
            System.out.println("Received message : " + msg.getValue());
            msg.getMessageId();
        }

        consumer.close();
        producer.close();
        client.close();
    }

    public static void main(String[] args) {
        ExclusiveSubscriptionExample example = new ExclusiveSubscriptionExample();
        example.run(args);
    }
}
