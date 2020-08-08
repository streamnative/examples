# Overview

This document describes how to produce messages to and consume messages from a Apache Pulsar cluster using the Java Producer and Consumer API.

# Prerequisites

- Java: 1.8+
- Pulsar broker: 2.7.0-742fc5c9b+
- Get the `serviceURL` of your StreamNative Cloud Pulsar cluster: [How to get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls)
- Get the OAuth2 `privateKey` of a service account to access your StreamNative Cloud Pulsar cluster: [How to get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters)
- Get the `audience` of the your StreamNative Cloud Pulsar cluster: [How to get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters)
- Get the `issuerUrl` of the your StreamNative Cloud Pulsar cluster: [How to get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters)

> You can get this tarball from [bintray](https://bintray.com/streamnative/maven/org.apache.pulsar/2.7.0-742fc5c9b). When Pulsar 2.6.1 is released, you can also use the official 2.6.1 version.

# Example

In this example, the producer publishes messages to the `topic-1` in your Pulsar cluster.
The content of each message payload is a combination of `my-message-` and a digital (0-9) (e.g: `my-message-0`).
The consumer receives the message from the `topic-1` and `acknowledges` each received message.

> Tips: The following code example uses the OAuth2 connection method. If you want to connect to the Pulsar cluster using Token, please refer to the implementation of **connectByToken.java**.

1. Run the consumer.

```shell script
# Compile the Java code
$ mvn clean package

# Run the consumer
$ mvn exec:java -Dexec.mainClass="io.streamnative.examples.oauth2.SampleConsumer" \
      -Dexec.args="--serviceUrl pulsar+ssl://cloud.streamnative.dev:6651 --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name --issuerUrl https://cloud.streamnative.dev --privateKey file:///path/to/private/key/file.txt"
```

Output:

```text
Receive message my-message-0 and message ID 1121:0:-1:0
Receive message my-message-1 and message ID 1121:1:-1:0
Receive message my-message-2 and message ID 1121:2:-1:0
Receive message my-message-3 and message ID 1121:3:-1:0
Receive message my-message-4 and message ID 1121:4:-1:0
Receive message my-message-5 and message ID 1121:5:-1:0
Receive message my-message-6 and message ID 1121:6:-1:0
Receive message my-message-7 and message ID 1121:7:-1:0
Receive message my-message-8 and message ID 1121:8:-1:0
Receive message my-message-9 and message ID 1121:9:-1:0
```

2. Run the producer.

```shell script
# Compile the Java code
$ mvn clean package

# Run the producer
$ mvn exec:java -Dexec.mainClass="io.streamnative.examples.oauth2.SampleConsumer" \
      -Dexec.args="--serviceUrl pulsar+ssl://cloud.streamnative.dev:6651 --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name --issuerUrl https://cloud.streamnative.dev --privateKey file:///path/to/private/key/file.txt"
```

Output:

```text
Publish my-message-0 and message ID 1121:0:-1:0
Publish my-message-1 and message ID 1121:1:-1:0
Publish my-message-2 and message ID 1121:2:-1:0
Publish my-message-3 and message ID 1121:3:-1:0
Publish my-message-4 and message ID 1121:4:-1:0
Publish my-message-5 and message ID 1121:5:-1:0
Publish my-message-6 and message ID 1121:6:-1:0
Publish my-message-7 and message ID 1121:7:-1:0
Publish my-message-8 and message ID 1121:8:-1:0
Publish my-message-9 and message ID 1121:9:-1:0
```

