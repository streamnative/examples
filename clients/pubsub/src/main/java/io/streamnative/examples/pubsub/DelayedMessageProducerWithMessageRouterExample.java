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

import io.streamnative.examples.common.ExampleRunner;
import io.streamnative.examples.common.ProducerFlags;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageRouter;
import org.apache.pulsar.client.api.MessageRoutingMode;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TopicMetadata;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.apache.pulsar.client.impl.Hash;
import org.apache.pulsar.client.impl.JavaStringHash;
import org.apache.pulsar.client.impl.MessageImpl;

/**
 * Example that demonstrates how to custom a message router use delayed message delivery feature.
 **/
@Slf4j
public class DelayedMessageProducerWithMessageRouterExample extends ExampleRunner<ProducerFlags> {

    public static void main(String[] args) {
        DelayedMessageProducerWithMessageRouterExample example =
                new DelayedMessageProducerWithMessageRouterExample();

        example.run(args);
    }

    @Override
    protected String name() {
        return DelayedMessageProducerWithMessageRouterExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "An example that demonstrates how to custom a message router use delayed message delivery feature.";
    }

    @Override
    protected ProducerFlags flags() {
        return new ProducerFlags();
    }

    @Override
    protected void run(ProducerFlags flags) throws Exception {
        try (PulsarClient client = PulsarClient.builder()
                .serviceUrl(flags.binaryServiceUrl)
                .build()) {

            try (Producer<String> producer = client.newProducer(Schema.STRING)
                    .topic(flags.topic)
                    .messageRoutingMode(MessageRoutingMode.CustomPartition)
                    .messageRouter(new CustomMessageRouter()).create()) {
                final int numMessages = Math.max(flags.numMessages, 1);

                // Immediate delivery
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                            .value("Immediate delivery message " + i)
                            .sendAsync();
                }
                producer.flush();

                // Delay 1 ~ numMessages seconds using DeliverAfter
                for (int i = 0; i < numMessages; i++) {
                    int delayTime = (i % numMessages) + 1;
                    producer.newMessage()
                            .value("DeliverAfter message " + i + ", delay time : " + delayTime)
                            .deliverAfter(delayTime, TimeUnit.SECONDS)
                            .sendAsync();
                }
                producer.flush();
            }

        }
    }

    private static class CustomMessageRouter implements MessageRouter {

        private final Hash hash = JavaStringHash.getInstance();

        @Override
        public int choosePartition(Message<?> msg, TopicMetadata metadata) {
            if (msg instanceof MessageImpl) {
                MessageImpl<?> message = (MessageImpl<?>) msg;
                long deliverAtTime = message.getMessageBuilder().getDeliverAtTime();
                // We can route message by deliverAtTime if deliverAtTime exists,
                // in this case just mod by partition nums.
                if (deliverAtTime != 0) {
                    long deliverAtTimeSeconds =
                            Duration.ofMillis(deliverAtTime - System.currentTimeMillis()).getSeconds();
                    if (deliverAtTimeSeconds < 0) {
                        deliverAtTimeSeconds = 0;
                    }
                    return (int) (deliverAtTimeSeconds % metadata.numPartitions());
                }
            }
            // When message is normal message run this follow strategy.
            // Just for demo, in production you may need choose suitable strategy.
            if (msg.hasKey() && msg.getSequenceId() < 0) {
                return hash.makeHash(msg.getKeyBytes()) % metadata.numPartitions();
            }
            if (msg.getSequenceId() >= 0) {
                return (int) (msg.getSequenceId() % metadata.numPartitions());
            }
            return hash.makeHash(msg.getData()) % metadata.numPartitions();
        }
    }
}
