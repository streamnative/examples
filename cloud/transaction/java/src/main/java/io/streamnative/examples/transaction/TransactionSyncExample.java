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

        /**
         * This is an example code that utilizes Pulsar's transaction feature to consume data from an input topic,
         * process it, and output messages to two different output topics while ensuring transaction atomicity.
         *
         * The code initializes Pulsar producers and consumers for the input and output topics.
         * Two messages are produced to be consumed from the input topic, and for each consumed message,
         * two messages are produced to each of the output topics within a transaction. The input message is
         * acknowledged with the transaction before committing it, and in case of a transaction conflict exception,
         * the message is negatively acknowledged and the transaction is aborted.
         *
         * This example demonstrates the importance of atomicity when processing messages from a
         * data source and outputting them to multiple topics. By using transactions, the entire process is
         * treated as a single unit of work, and either succeeds or fails together,
         * ensuring data consistency and reliability.
         */

        String inputTopic = "persistent://public/default/input-topic";
        String outputTopicOne = "persistent://public/default/output-topic-1";
        String outputTopicTwo = "persistent://public/default/output-topic-2";

        PulsarClient client = PulsarClient.builder()
                // Create a Pulsar client and enable Transactions.
                .enableTransaction(true)
                .serviceUrl(jct.serviceUrl)
                .authentication(
                        AuthenticationFactoryOAuth2.clientCredentials(new URL(jct.issuerUrl), new URL(jct.credentialsUrl), jct.audience))
                .build();

        // Create two producers to produce messages to input and output topics.
        ProducerBuilder<String> producerBuilder = client.newProducer(Schema.STRING);
        Producer<String> inputProducer = producerBuilder.topic(inputTopic)
                .sendTimeout(0, TimeUnit.SECONDS).create();
        Producer<String> outputProducerOne = producerBuilder.topic(outputTopicOne)
                .sendTimeout(0, TimeUnit.SECONDS).create();
        Producer<String> outputProducerTwo = producerBuilder.topic(outputTopicTwo)
                .sendTimeout(0, TimeUnit.SECONDS).create();
        // Create two consumers to consume messages for input and output topics.
        Consumer<String> inputConsumer = client.newConsumer(Schema.STRING)
                .subscriptionName("test").topic(inputTopic).subscribe();
        Consumer<String> outputConsumerOne = client.newConsumer(Schema.STRING)
                .subscriptionName("test").topic(outputTopicOne).subscribe();
        Consumer<String> outputConsumerTwo = client.newConsumer(Schema.STRING)
                .subscriptionName("test").topic(outputTopicTwo).subscribe();

        int count = 2;
        // Produce messages to topics.
        for (int i = 0; i < count; i++) {
            inputProducer.send("Hello Pulsar! count : " + i);
        }

        // consume messages and produce to output topics with transaction
        for (int i = 0; i < count; i++) {

            // The consumer successfully receives messages. Then, create a transaction.
            Message<String> message = inputConsumer.receive();
            Transaction txn = null;
            try {
                txn = client.newTransaction()
                        .withTransactionTimeout(10, TimeUnit.SECONDS).build().get();
                // process the message here...

                // The producers produce messages to output topics with the transaction
                outputProducerOne.newMessage(txn).value("Hello Pulsar! outputTopicOne count : " + i).send();
                outputProducerTwo.newMessage(txn).value("Hello Pulsar! outputTopicTwo count : " + i).send();

                // The consumers acknowledge the input message with the transaction
                inputConsumer.acknowledgeAsync(message.getMessageId(), txn).get();
                // commit the transaction
                txn.commit();
            } catch (ExecutionException e) {
                if (!(e.getCause() instanceof PulsarClientException.TransactionConflictException)) {
                    // if not TransactionConflictException,
                    // we should redeliver or negativeAcknowledge this message
                    // if you don't redeliver or negativeAcknowledge, the message will not receive again
                    inputConsumer.negativeAcknowledge(message);
                }

                // if transaction has been created, should abort this transaction
                if (txn != null) {
                    txn.abort();
                }
            }
        }

        // consume messages from output topics and print them
        for (int i = 0; i < count; i++) {
            Message<String> message =  outputConsumerOne.receive();
            System.out.println("Receive transaction message: " + message.getValue());
        }

        for (int i = 0; i < count; i++) {
            Message<String> message =  outputConsumerTwo.receive();
            System.out.println("Receive transaction message: " + message.getValue());
        }

        // release the io resource
        inputProducer.close();
        inputConsumer.close();
        outputConsumerOne.close();
        outputConsumerTwo.close();
        outputProducerOne.close();
        outputProducerTwo.close();
        client.close();
    }
}
