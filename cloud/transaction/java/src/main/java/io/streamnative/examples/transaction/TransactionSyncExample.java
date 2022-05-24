// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package io.streamnative.examples.transaction;

import com.beust.jcommander.JCommander;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.transaction.Transaction;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TransactionSyncExample {
    private static final Logger log = LoggerFactory.getLogger(TransactionSyncExample.class);
    public static void main(String[] args) throws Exception {
        JCommanderPulsar jct = new JCommanderPulsar();
        JCommander jCommander = new JCommander(jct, args);
        if (jct.help) {
            jCommander.usage();
            return;
        }

        String topic1 = "persistent://public/default/topic-1";
        String topic2 = "persistent://public/default/topic-2";

        PulsarClient client = PulsarClient.builder()
                .enableTransaction(true)
                .serviceUrl(jct.serviceUrl)
                .authentication(
                        AuthenticationFactoryOAuth2.clientCredentials(new URL(jct.issuerUrl), new URL(jct.credentialsUrl), jct.audience))
                .build();

        ProducerBuilder<String> producerBuilder = client.newProducer(Schema.STRING).enableBatching(false);
        Producer<String> producer1 = producerBuilder.topic(topic1).sendTimeout(0, TimeUnit.SECONDS).create();
        Producer<String> producer2 = producerBuilder.topic(topic2).sendTimeout(0, TimeUnit.SECONDS).create();

        Consumer<String> consumer1 = client.newConsumer(Schema.STRING).subscriptionName("test").topic(topic1).subscribe();
        Consumer<String> consumer2 = client.newConsumer(Schema.STRING).subscriptionName("test").topic(topic2).subscribe();

        int count = 2;
        // First prepare two messages that can be consumed
        for (int i = 0; i < count; i++) {
            producer1.send("Hello Pulsar! count : " + i);
        }

        for (int i = 0; i < count; i++) {
            // Consume two messages and send two messages to topic-2 with the same transaction
            // Receive the message first, and then start the transaction after receiving the message.
            // If the transaction is started first and no message is received for
            // a long time, it will cause the transaction to time out.
            Message<String> message = consumer1.receive();
            Transaction txn = null;
            try {
                txn = client.newTransaction()
                        .withTransactionTimeout(10, TimeUnit.SECONDS).build().get();
                // new transaction success, then you can do you own op
                producer2.newMessage(txn).value("Hello Pulsar! count : " + i).send();
                consumer1.acknowledgeAsync(message.getMessageId(), txn).get();
                txn.commit();
            } catch (ExecutionException e) {
                if (!(e.getCause() instanceof PulsarClientException.TransactionConflictException)) {
                    // if not TransactionConflictException,
                    // we should redeliver or negativeAcknowledge this message
                    // if you don't redeliver or negativeAcknowledge, the message will not receive again
                    // ((ConsumerImpl<String>)consumer1).redeliverUnacknowledgedMessages(Collections.singleton(message.getMessageId()));
                    // ((MultiTopicsConsumerImpl<String>)consumer1).redeliverUnacknowledgedMessages(Collections.singleton(message.getMessageId()));
                    consumer1.negativeAcknowledge(message);
                }
                if (txn != null) {
                    txn.abort();
                }
            }
        }

        for (int i = 0; i < count; i++) {
            Message<String> message =  consumer2.receive();
            System.out.println("Receive transaction message: " + message.getValue());
        }

        // release the io resource
        consumer2.close();
        consumer1.close();
        producer1.close();
        producer2.close();
        client.close();
    }
}
