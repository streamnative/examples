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
#include <pulsar/Authentication.h>
#include <pulsar/Client.h>

using namespace pulsar;

int main(int argc, char *argv[]) {
    ClientConfiguration config;
    std::string oauthParams = argv[2];

    config.setAuth(pulsar::AuthOauth2::create(oauthParams));

    Client client(argv[1], config);

    Producer producer;
    Result result = client.createProducer("persistent://public/default/my-topic", producer);
    if (result != ResultOk) {
        std::cout << "Error creating producer: " << result << "\n";
        return -1;
    }

    // Send synchronously
    Message msg = MessageBuilder().setContent("content").build();
    Result res = producer.send(msg);
    std::cout << "Message sent: " << res << "\n";

    client.close();
}
