# Overview

This document describes how to produce message to and consume message from a Pulsar cluster using [Apache pulsar-client-node](https://github.com/apache/pulsar-client-node).

# Prerequisites

- Get the `SERVICE_URL` of your StreamNative Cloud Pulsar cluster: [How to get service URL](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls)
- Get the `AUTH_PARAMS` of your StreamNative Cloud Pulsar cluster: [How to get token options](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-token-authentication-parameters)

Pulsar Node.js client library is based on the C++ client library. Follow the instructions for
[C++ library](https://pulsar.apache.org/docs/en/client-libraries-cpp/) for installing the binaries through
[RPM](https://pulsar.apache.org/docs/en/client-libraries-cpp/#rpm),
[Deb](https://pulsar.apache.org/docs/en/client-libraries-cpp/#deb) or
[Homebrew packages](https://pulsar.apache.org/docs/en/client-libraries-cpp/#macos).

> #### Note
> You need to install both the pulsar-client library and the pulsar-client-dev library.

Also, this library works only in Node.js 10.x or later because it uses the
[node-addon-api](https://github.com/nodejs/node-addon-api) module to wrap the C++ library.

## Install 

### Install pulsar-client in your project:

```shell
$ npm install pulsar-client
```

# Example

In this example, the producer publishes data to the `my-topic` in your Pulsar cluster.
The content of each message payload is a combination of `my-message-` and a digital (0-9) (e.g: `my-message-0`).
The consumer receives the message from the `my-topic` and `acknowledges` each received message.

1. Run the consumer, and start to receiving the message from `my-topic`:

```bash
$ cd cloud/node
$ export SERVICE_URL="pulsar+ssl://cloud.streamnative.dev:6651"
$ export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
$ node sample_consumer.js
```

Output:

```text
my-message-0
my-message-1
my-message-3
my-message-2
my-message-4
my-message-5
my-message-7
my-message-6
my-message-8
my-message-9
```

2. Run the producer and publish messages to the `my-topic`:

```bash
$ cd cloud/node
$ export SERVICE_URL="pulsar+ssl://cloud.streamnative.dev:6651"
$ export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
$ node sample_producer.js
```

Output:

```text
Sent message: my-message-0
Sent message: my-message-1
Sent message: my-message-2
Sent message: my-message-3
Sent message: my-message-4
Sent message: my-message-5
Sent message: my-message-6
Sent message: my-message-7
Sent message: my-message-8
Sent message: my-message-9
```
