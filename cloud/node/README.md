# Overview

This document describes how to produce message to and consume message from a Pulsar cluster using [Apache pulsar-client-node](https://github.com/apache/pulsar-client-node).

# Prerequisites

Pulsar Node.js client library is based on the C++ client library. Follow the instructions for [C++ library](https://pulsar.apache.org/docs/en/client-libraries-cpp/) for installing the binaries through [RPM](https://pulsar.apache.org/docs/en/client-libraries-cpp/#rpm),
[Deb](https://pulsar.apache.org/docs/en/client-libraries-cpp/#deb) or [Homebrew packages](https://pulsar.apache.org/docs/en/client-libraries-cpp/#macos). Also, this library works only in Node.js 10.x or higher because it uses the [node-addon-api](https://github.com/nodejs/node-addon-api) module to wrap the C++ library.

**Note**

> You need to install both the pulsar-client library and the pulsar-client-dev library.

# Install pulsar-client in your project

Use the following command to install pulsar-client in your project.

```shell
npm install pulsar-client
```

# Example

In this example, the Node.js producer publishes data to the `my-topic` in your Pulsar cluster. The consumer receives the message from the `my-topic` and acknowledges each received message.
The content of each message payload is a combination of `my-message-` and a digital (0-9) (e.g: `my-message-0`).

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Token authentication parameters. For details, see [Get Token authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-token-authentication-parameters).

3. Run the Node.js consumer to receive messages from the topic `my-topic`.

    ```bash
    cd cloud/node
    export SERVICE_URL="pulsar+ssl://streamnative.cloud:6651"
    export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
    node sample_consumer.js
    ```

    **Output**:

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

4. Run the Node.js producer to publish messages to the topic `my-topic`.

    ```bash
    cd cloud/node
    export SERVICE_URL="pulsar+ssl://streamnative.cloud:6651"
    export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
    node sample_producer.js
    ```

    **Output**:

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

4. Run the Node.js producer to publish messages to the topic `my-topic` by the oauth2.

    ```bash
    cd cloud/node
    export ISSER_URL = ""
    export PRIVATE_KEY = ""
    export AUDIENCE = ""
    export SERVICE_URL = ""
    ```

