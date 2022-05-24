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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.transaction.TransactionCoordinatorClientException;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.apache.pulsar.common.util.FutureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionExample {
    private static final Logger log = LoggerFactory.getLogger(TransactionExample.class);
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
        Producer<String> producer1 = producerBuilder.topic(topic1).create();
        Producer<String> producer2 = producerBuilder.topic(topic2).create();

        Consumer<String> consumer1 = client.newConsumer(Schema.STRING).subscriptionName("test").topic(topic1).subscribe();

        // First prepare 10 pieces of messages that can be consumed
        for (int i = 0; i < 10; i++) {
            producer1.send("Hello Pulsar!");
        }

        // Consume these 10 messages and send 10 messages to topic-2 with the same transaction
        for (int i = 0; i < 10; i++) {
            // Receive the message first, and then start the transaction after receiving the message.
            // If the transaction is started first and no message is received for
            // a long time, it will cause the transaction to time out.

            consumer1.receiveAsync().thenAccept(message -> {
                // receive message success then new transaction
                try {
                    client.newTransaction().withTransactionTimeout(10, TimeUnit.SECONDS).build().thenAccept(txn -> {
                        // new transaction success, then you can do you own op
                        List<CompletableFuture<?>> futures = new ArrayList<>();
                        // add send message with txn future to futures
                        futures.add(producer2.newMessage(txn).value("Hello Pulsar!").sendAsync());
                        // add ack message with txn future to futures
                        futures.add(consumer1.acknowledgeAsync(message.getMessageId(), txn).exceptionally(e -> {
                            if (!(e.getCause() instanceof PulsarClientException.TransactionConflictException)) {
                                // if not TransactionConflictException,
                                // we should redeliver or negativeAcknowledge this message
                                // if you don't redeliver or negativeAcknowledge, the message will not receive again
                                // ((ConsumerImpl<String>)consumer1).redeliverUnacknowledgedMessages(Collections.singleton(message.getMessageId()));
                                // ((MultiTopicsConsumerImpl<String>)consumer1).redeliverUnacknowledgedMessages(Collections.singleton(message.getMessageId()));
                                consumer1.negativeAcknowledge(message);
                            }
                            return null;
                        }));

                        FutureUtil.waitForAll(futures).thenRun(() -> {
                            // futures are all success, then can commit this transaction
                            txn.commit().thenRun(() -> {
                                log.info("txn : {} commit success!", txn);
                            }).exceptionally(e -> {
                                log.error("txn : {} commit fail!", txn);
                                // if not TransactionNotFoundException, you can commit again or abort
                                if (!(e.getCause() instanceof TransactionCoordinatorClientException.TransactionNotFoundException)) {
                                    txn.commit();
                                }
                                return null;
                            });
                        }).exceptionally(e -> {
                            // if futures has fail op, abort this txn
                            txn.abort();
                            return null;
                        });

                    }).exceptionally(e -> {
                        // new transaction fail, should redeliver this message or negativeAcknowledge
                        // if you don't redeliver or negativeAcknowledge, the message will not receive again
                        // ((ConsumerImpl<String>)consumer1).redeliverUnacknowledgedMessages(Collections.singleton(message.getMessageId()));
                        // ((MultiTopicsConsumerImpl<String>)consumer1).redeliverUnacknowledgedMessages(Collections.singleton(message.getMessageId()));
                        consumer1.negativeAcknowledge(message);
                        return null;
                    });
                } catch (PulsarClientException e) {
                    // client don't support transaction, please enable transaction enableTransaction(true)
                }
            });
        }
    }
}
