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

package io.streamnative.examples.oauth2;

import java.net.URL;
import org.apache.pulsar.client.api.*;

import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;

public class SampleConsumer {
    public static void main(String[] args) throws Exception {
        String issuerUrl = "https://dev-kt-aa9ne.us.auth0.com/oauth/token";
        String credentialsUrl = "file:///path/to/KeyFile.json";
        String audience = "https://dev-kt-aa9ne.us.auth0.com/api/v2/";
        String topic = "persistent://public/default/topic-1";


        PulsarClient client = PulsarClient.builder()
                .serviceUrl("pulsar+ssl://xxx.us-east4.yyy.test.g.sn2.dev:6651")
                .authentication(
                        AuthenticationFactoryOAuth2.clientCredentials(new URL(issuerUrl), new URL(credentialsUrl), audience))
                .build();

        Consumer<byte[]> consumer = client.newConsumer(Schema.BYTES)
                .topic(topic)
                .subscriptionName("sub-1")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

        for (int i = 0; i < 10; i++) {
            Message<byte[]> msg = consumer.receive();
            consumer.acknowledge(msg);
            System.out.println("Receive message " + new String(msg.getData()));
        }

        consumer.close();
        client.close();
    }
}
