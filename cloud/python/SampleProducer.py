#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.

import os
from pulsar import Client, AuthenticationToken

client = Client(os.environ.get('SERVICE_URL'), authentication=AuthenticationToken(os.environ.get('AUTH_PARAMS')))

producer = client.create_producer('my-topic')

for i in range(10):
    producer.send(('Hello-%d' % i).encode('utf-8'))
    print('send msg "hello-%d"' % i)

client.close()
