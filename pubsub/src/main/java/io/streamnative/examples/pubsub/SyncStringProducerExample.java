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
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;

/**
 * Example that demonstrates an producer that publish messages using
 * synchronous {@link Producer#send(Object)} method.
 **/
public class SyncStringProducerExample extends ExampleRunner<ProducerFlags> {
    @Override
    protected String name() {
        return SyncStringProducerExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "Example that demonstrates how to use dead letter topic.";
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
                    .enableBatching(false)
                    .topic(flags.topic)
                    .create()) {

                final int numMessages = Math.max(flags.numMessages, 1);

                // publish messages
                for (int i = 0; i < numMessages; i++) {
                    producer.newMessage()
                            .value("value-" + i)
                            .sendAsync();
                }
                producer.flush();
            }
        }
    }

    public static void main(String[] args) {
        SyncStringProducerExample example = new SyncStringProducerExample();
        example.run(args);
    }
}
