# Overview

This document describes how to produce message to and consume message from a Pulsar cluster using [Apache pulsar-client-python](https://github.com/apache/pulsar/tree/master/pulsar-client-cpp/python).

# Prerequisites

N/A

# Install pulsar-client library

This section describes how to install the pulsar-client library through pip or the source code.

## Install pulsar-client library using pip

To install the pulsar-client library as a pre-built package using the [pip](https://pip.pypa.io/en/stable/) package manager.

```bash
pip install pulsar-client==2.6.0
```

## Install pulsar-client library from source code

To install the pulsar-client library by building from source, follow the [instructions](https://pulsar.apache.org/docs/en/client-libraries-cpp#compilation) and compile the Pulsar C++ client library. That builds the Python binding for the library.

Use the following commands to install the built Python bindings.

```shell script
git clone https://github.com/apache/pulsar
cd pulsar/pulsar-client-cpp/python
sudo python setup.py install
```

# Example

In this example, the Python producer publishes data to the `my-topic` in your Pulsar cluster. The consumer receives the message from the `my-topic` and `acknowledges` each received message.
The content of each message payload is a combination of `hello-` and a digital (0-9) (e.g: `hello-0`).

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the token authentication parameters. For details, see [Get token authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-token-authentication-parameters).

3. Run the Python consumer to receive messages from the topic  `my-topic`.

    ```bash
    cd cloud/python
    export SERVICE_URL="pulsar+ssl://cloud.streamnative.dev:6651"
    export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
    python SampleConsumer.py
    ```

    **Output**:

    ```text
    Received message 'Hello-0' id='(250,0,-1,-1)'
    Received message 'Hello-1' id='(250,1,-1,-1)'
    Received message 'Hello-2' id='(250,2,-1,-1)'
    Received message 'Hello-3' id='(250,3,-1,-1)'
    Received message 'Hello-4' id='(250,4,-1,-1)'
    Received message 'Hello-5' id='(250,5,-1,-1)'
    Received message 'Hello-6' id='(250,6,-1,-1)'
    Received message 'Hello-7' id='(250,7,-1,-1)'
    Received message 'Hello-8' id='(250,8,-1,-1)'
    Received message 'Hello-9' id='(250,9,-1,-1)'
    ```

4. Run the Python producer to publish messages to the topic `my-topic`.

    ```bash
    cd cloud/python
    export SERVICE_URL="pulsar+ssl://cloud.streamnative.dev:6651"
    export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
    python SampleProducer.py
    ```

    **Output**:

    ```text
    send msg "hello-0"
    send msg "hello-1"
    send msg "hello-2"
    send msg "hello-3"
    send msg "hello-4"
    send msg "hello-5"
    send msg "hello-6"
    send msg "hello-7"
    send msg "hello-8"
    send msg "hello-9"
    ```
