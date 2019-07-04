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
import org.apache.commons.lang.time.DateUtils;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Example that demonstrates how to use delayed message delivery feature.
 **/
public class DelayedMessageProducerExample extends ExampleRunner<ProducerFlags> {
    @Override
    protected String name() {
        return DelayedMessageProducerExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "An example demonstrates how to use delayed message delivery feature";
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
                    .create()) {

                final int numMessages = Math.max(flags.numMessages, 1);

                // immediate delivery
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                            .value("Immediate delivery message " + i + "; produce-time = " + new Date())
                            .sendAsync();
                }
                producer.flush();

                // DeliverAfter 5 seconds
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                            .value("DeliverAfter message " + i + "; produce-time = " + new Date())
                            .deliverAfter(5, TimeUnit.SECONDS)
                            .sendAsync();
                }
                producer.flush();

                // DeliverAfter 10 seconds using DeliverAt
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                            .value("DeliverAt message " + i + "; produce-time = " + new Date())
                            .deliverAt(DateUtils.addSeconds(new Date(), 10).getTime())
                            .send();
                }

                producer.flush();
            }

        }
    }

    public static void main(String[] args) {
        DelayedMessageProducerExample example = new DelayedMessageProducerExample();
        example.run(args);
    }
}
