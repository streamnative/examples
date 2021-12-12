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
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;

public class ConnectByOauth2 {
    public static void main(String[] args) throws Exception {
        String issuerUrl = "https://auth.streamnative.cloud/oauth/token";
        String credentialsUrl = "file:///path/to/KeyFile.json";
        // String credentialsUrl = "data:application/json;base64,eyJjbGllbnRfaWQiOiJjbGllbnQtaWQiLCJjbGllbnRfc2VjcmV0IjoiY2xpZW50LXNlY3JldCJ9Cg==";
        String audience = "https://auth.streamnative.cloud/api/v2/";

        PulsarClient client = PulsarClient.builder()
                .serviceUrl("pulsar+ssl://streamnative.cloud:6651")
                .authentication(
                        AuthenticationFactoryOAuth2.clientCredentials(new URL(issuerUrl), new URL(credentialsUrl), audience))
                .build();

        client.close();
    }
}
