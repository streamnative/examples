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

from pulsar import Client, AuthenticationOauth2
import argparse


# OAuth2Consumer provides a simple way to consume messages.
class OAuth2Consumer:

    def __init__(self, args):
        self.args = args
        self.client = Client(args.service_url, authentication=AuthenticationOauth2(args.auth_params))

    def consume(self):
        c = self.client.subscribe(args.topic, args.sub)
        while args.number > 0:
            try:
                msg = c.receive()
                print("Received message '{}' id='{}'".format(msg.data(), msg.message_id()))
                # Acknowledge successful processing of the message
                c.acknowledge(msg)
                args.number -= 1
            except:
                # Message failed to be processed
                c.negative_acknowledge(msg)

    def close(self):
        self.client.close()


if __name__ == '__main__':
    parse = argparse.ArgumentParser(prog='OAuth2Consumer.py')
    parse.add_argument('-su', '--service-url', dest='service_url', type=str, required=True,
                       help='The pulsar service you want to connect to')
    parse.add_argument('-t', '--topic', dest='topic', type=str, required=True,
                       help='The topic you want to consume from')
    parse.add_argument('-sn', '--subscription-name', dest='sub', type=str, default='oauth2-consumer',
                       help='The subscription name for the oauth2 consumer')
    parse.add_argument('-n', '--number', dest='number', type=int, default=1,
                       help='The number of message you want to receive')
    parse.add_argument('--auth-params', dest='auth_params', type=str, default="",
                       help='The auth params which you need to configure the client')
    args = parse.parse_args()

    consumer = OAuth2Consumer(args)
    consumer.consume()
    consumer.close()
