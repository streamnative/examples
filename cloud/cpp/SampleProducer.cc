/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
#include <iostream>

#include <pulsar/Client.h>

#include <lib/LogUtils.h>

DECLARE_LOG_OBJECT()

using namespace pulsar;

int main() {
    ClientConfiguration config;
    std::string params = R"({
    "issuer_url": "https://dev-kt-aa9ne.us.auth0.com/oauth/token",
    "private_key": "../../pulsar-broker/src/test/resources/authentication/token/cpp_credentials_file.json",
    "audience": "https://dev-kt-aa9ne.us.auth0.com/api/v2/"})";

    config.setAuth(pulsar::AuthOauth2::create(params));

    Client client("pulsar+ssl://mhlcluster.mhltest.us-east4.streamnative.test.g.sn2.dev:6651", config);

    Producer producer;
    Result result = client.createProducer("persistent://public/default/topic-1", producer);
    if (result != ResultOk) {
        LOG_ERROR("Error creating producer: " << result);
        return -1;
    }

    // Send synchronously
    Message msg = MessageBuilder().setContent("content").build();
    Result res = producer.send(msg);
    LOG_INFO("Message sent: " << res);

    client.close();
}
