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
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;

/**
 * Example that demonstrates how to produce multiple messages to one partition in
 * an atomic way.
 */
public class SinglePartitionAtomicProducerExample extends ExampleRunner<ProducerFlags> {

    @Override
    protected String name() {
        return SinglePartitionAtomicProducerExample.class.getSimpleName();
    }

    @Override
    protected String description() {
        return "An example demonstrates how to produce multiple messages to one partition in an atomic way";
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
                // enable batching in a way that the producer can control how to batch
                // the messages and when to flush the batch. This allows producing multiple
                // messages in an atomic way
                .enableBatching(true)
                .batchingMaxPublishDelay(Long.MAX_VALUE, TimeUnit.MILLISECONDS)
                .batchingMaxMessages(Integer.MAX_VALUE)
                .create()) {

                final int numMessages = Math.max(flags.numMessages, 1);

                // send a message to force establish the connection to get around
                // the issue around producer#flush()
                // {@link https://github.com/apache/pulsar/issues/4642}
                producer.newMessage()
                    .value("value-0")
                    .sequenceId(0L)
                    .send();

                for (int i = 1; i <= numMessages; i++) {
                    producer.newMessage()
                        .value("value-" + i)
                        // the sequence id is used as an unique identifier for
                        // de-duplication
                        .sequenceId(i)
                        .sendAsync();
                }

                // `flush` flushes all the messages in one message batch
                // so the messages appended above will either all succeed or all fail.
                producer.flush();

                // try to produce duplicated messages (with same sequence id)
                // to simulate retries after failures
                for (int i = 1; i <= numMessages; i++) {
                    producer.newMessage()
                        .value("duplicated-value-" + i)
                        .sequenceId(i)
                        .sendAsync();
                }
                producer.flush();

                // try to produce more messages
                for (int i = 1; i <= numMessages; i++) {
                    producer.newMessage()
                        .value("value-" + (numMessages + i))
                        .sequenceId(numMessages + i)
                        .sendAsync();
                }
                producer.flush();
            }

        }

    }

    public static void main(String[] args) {
        SinglePartitionAtomicProducerExample example = new SinglePartitionAtomicProducerExample();
        example.run(args);
    }
}
