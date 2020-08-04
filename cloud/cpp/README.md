# Overview

Produce message to and consume message from a Pulsar cluster using [Apache pulsar-cpp-client](https://github.com/apache/pulsar/tree/master/pulsar-client-cpp).

# Prerequisites

## Linux

Since 2.1.0 release, Pulsar ships pre-built RPM and Debian packages. You can download and install those packages directly.

For more information, refer to [here](https://pulsar.apache.org/docs/en/client-libraries-cpp/#supported-platforms).

## MacOS

Pulsar releases are available in the Homebrew core repository. You can install the C++ client library with the following command. The package is installed with the library and headers.

```shell script
$ brew install libpulsar
```

# Example

In this example, the producer will publish data to the `topic-1` in your Pulsar cluster.
The content of each message payload is  `content`.
The consumer will receive the message from the `topic-1` and `ack` the receipt of each message received.

> Tips: The following code example uses the OAuth2 connection method. If you want to connect to the Pulsar cluster using Token, please refer to the implementation of **connectByToken.cc**.

1. Run the consumer, and start to receiving the message from `topic-1`:

```bash
$ g++ --std=c++11 SampleConsumer.cc -o SampleConsumer -I/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/include -lpulsar -L/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/lib
$ ./SampleConsumer
```

Output:

```text
Received: Message(prod=standalone-1-2, seq=0, publish_time=1596467242114, payload_size=7, msg_id=(71,0,-1,0), props={})  with payload 'content'
```

2. Run the producer and publish messages to the `topic-1`:

```bash
$ g++ --std=c++11 SampleProducer.cc -o SampleProducer -I/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/include -lpulsar -L/usr/local/Cellar/libpulsar/{PULSAR_VERSION}/lib
$ ./SampleProducer
```

Output:

```text
Message sent: OK
```
