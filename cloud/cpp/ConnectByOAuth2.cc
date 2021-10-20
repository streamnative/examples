
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
#include <pulsar/Authentication.h>
#include <pulsar/Client.h>

using namespace pulsar;

int main() {
    // C++ client provides two ways to construct an AuthOauth2 object
    // 1. Create by ParamMap (std::map<std::string, std::string>), this way is more simple and efficient
    ParamMap params;
    params["issuer_url"] = "http://cloud/oauth/token";
    params["private_key"] = "/resources/authentication/token/cpp_credentials_file.json";
    params["audience"] = "https://cloud.auth0.com/api/v2/";
    params["scope"] = "api://pulsar-cluster-1/.default";  // scope is optional

    Client client1("pulsar+ssl://streamnative.cloud:6651",
                   ClientConfiguration().setAuth(AuthOauth2::create(params)));
    client1.close();

    // 2. Create by JSON string, it will be parsed to a ParamMap internally
    std::string paramsJson = R"({
    "issuer_url": "https://cloud/oauth/token",
    "private_key": "/resources/authentication/token/cpp_credentials_file.json",
    "audience": "https://cloud.auth0.com/api/v2/",
    "scope": "api://pulsar-cluster-1/.default"})";  // scope is optional

    Client client2("pulsar+ssl://streamnative.cloud:6651",
                   ClientConfiguration().setAuth(AuthOauth2::create(paramsJson)));
    client2.close();
}
