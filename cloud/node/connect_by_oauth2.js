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

const Pulsar = require('pulsar-client');

const issuer_url = process.env.ISSER_URL;
const private_key = process.env.PRIVATE_KEY;
const audience = process.env.AUDIENCE;
const scope = process.env.SCOPE;
const service_url = process.env.SERVICE_URL;
const client_id = process.env.CLIENT_ID;
const client_secret = process.env.CLIENT_SECRET;

(async () => {
    const params = {
        issuer_url: issuer_url
    }
    if (private_key.length > 0) {
        params['private_key'] = private_key
    } else {
        params['client_id'] = client_id
        params['client_secret'] = client_secret
    }
    if (audience.length > 0) {
        params['audience'] = audience
    }
    if (scope.length > 0) {
        params['scope'] = scope
    }
    const auth = new Pulsar.AuthenticationOauth2({
        issuer_url: issuer_url,
        private_key: private_key,
        audience: audience,
    });

    // Create a client
    const client = new Pulsar.Client({
        serviceUrl: service_url,
        tlsAllowInsecureConnection: true,
        authentication: auth,
    });

    await client.close();
})();
 