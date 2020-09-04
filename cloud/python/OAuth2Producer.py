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


# SampleProducer provides a simple way to produce messages.
class OAuth2Producer:

    def __init__(self, args):
        self.args = args
        self.client = Client(args.service_url, authentication=AuthenticationOauth2(args.auth_params))

    def produce(self):
        p = self.client.create_producer(args.topic)
        num = args.number
        while num > 0:
            msg = 'message {} from oauth2 producer'.format(args.number-num)
            p.send(msg.encode('utf-8'))
            print('Produce message \'{}\' to the pulsar service successfully.'.format(msg))
            num -= 1

    def close(self):
        self.client.close()


if __name__ == '__main__':
    parse = argparse.ArgumentParser(prog='OAuth2Producer.py')
    parse.add_argument('-su', '--service-url', dest='service_url', type=str, required=True,
                       help='The pulsar service you want to connect to')
    parse.add_argument('-t', '--topic', dest='topic', type=str, required=True,
                       help='The topic you want to produce to')
    parse.add_argument('-n', '--number', dest='number', type=int, default=1,
                       help='The number of message you want to produce')
    parse.add_argument('--auth-params', dest='auth_params', type=str, default="",
                       help='The auth params which you need to configure the client')
    args = parse.parse_args()

    producer = OAuth2Producer(args)
    producer.produce()
    producer.close()
