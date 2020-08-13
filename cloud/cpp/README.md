# Overview

Produce message to and consume message from a Pulsar cluster using [Apache pulsar-cpp-client](https://github.com/apache/pulsar/tree/master/pulsar-client-cpp).

# Prerequisites

- CPP Client: 2.6.1+

## Linux

Since 2.1.0 release, Pulsar ships pre-built RPM and Debian packages. You can download and install those packages directly.

For more information, refer to [here](https://pulsar.apache.org/docs/en/client-libraries-cpp/#supported-platforms).

## MacOS

Pulsar releases are available in the Homebrew core repository. You can install the C++ client library with the following command. The package is installed with the library and headers.

```shell script
$ brew install libpulsar
```

# Example

**Note**

> This example shows how to connect to a Pulsar cluster through the OAuth2 authentication plugin. To connect to a Pulsar cluster through the token  authentication plugin, see the implementation of [ConnectByToken.cc](https://github.com/streamnative/pulsar-examples/blob/master/cloud/cpp/ConnectByToken.cc).

In this example, the CPP producer publishes data to the `my-topic` in your Pulsar cluster. The consumer receives the message from the `my-topic` and acknowledges each received message.
The content of each message payload is  `content`.

1. Get the Pulsar service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Run the CPP consumer to receive messages from `my-topic`:

```bash
$ g++ --std=c++11 SampleConsumer.cc -o SampleConsumer -I/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/include -lpulsar -L/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/lib
$ ./SampleConsumer pulsar+ssl://cloud.streamnative.dev:6651 '{"issuer_url": "https://cloud.streamnative.dev", "private_key": "/path/to/private/key/file.json", "audience": "urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name"}'
```

Replace the Oauth2 parameters (`issuer_url`, `audience`, and `private_key`) and the `args[1](serviceURL)` with the values that you get from Step 1 and Step 2 respectively.

Output:

```text
Received: Message(prod=standalone-1-2, seq=0, publish_time=1596467242114, payload_size=7, msg_id=(71,0,-1,0), props={})  with payload 'content'
```

4. Run the CPP producer and publish messages to the topic `my-topic`.

```bash
$ g++ --std=c++11 SampleProducer.cc -o SampleProducer -I/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/include -lpulsar -L/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/lib
$ ./SampleProducer pulsar+ssl://cloud.streamnative.dev:6651 '{"issuer_url": "https://cloud.streamnative.dev", "private_key": "/path/to/private/key/file.json", "audience": "urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name"}'
```

Output:

```text
Message sent: OK
```
