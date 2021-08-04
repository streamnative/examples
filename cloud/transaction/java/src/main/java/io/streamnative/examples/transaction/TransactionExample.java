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
import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;

import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.transaction.Transaction;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;

public class TransactionExample {
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
        Consumer<String> consumer2 = client.newConsumer(Schema.STRING).subscriptionName("test").topic(topic2).subscribe();
        int retryCount = 10;

        for (int i = 0; i < retryCount; i++) {
            try {
                Transaction transaction = client.newTransaction().withTransactionTimeout(10, TimeUnit.SECONDS).build().get();

                producer1.send("Hello Pulsar!");

                Message<String> message = consumer1.receive();

                consumer1.acknowledgeAsync(message.getMessageId(), transaction);

                producer2.send(message.getValue());

                transaction.commit().get();

                System.out.println("Receive transaction message: " + consumer2.receive( ).getValue());
                break;
            } catch (Exception ignored) {
                // wait tc load
                Thread.sleep(1000);
            }
        }

        consumer1.close();
        consumer2.close();
        producer1.close();
        producer2.close();
        client.close();
    }
}
