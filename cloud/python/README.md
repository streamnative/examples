# Overview

This document describes how to produce message to and consume message from a Pulsar cluster using [Apache pulsar-client-python](https://github.com/apache/pulsar/tree/master/pulsar-client-cpp/python).

# Prerequisites

- Get the `SERVICE_URL` of your StreamNative Cloud Pulsar cluster: [How to get service URL](#How to get service URL)
- Get the `AUTH_PARAMS` of your StreamNative Cloud Pulsar cluster: [How to get token options](#How to get token options)

## Install using pip

To install the pulsar-client library as a pre-built package using the [pip](https://pip.pypa.io/en/stable/) package manager:

```bash
$ pip install pulsar-client==2.6.0
```

## Install from source

To install the pulsar-client library by building from source, follow [instructions](https://pulsar.apache.org/docs/en/client-libraries-cpp#compilation) and compile the Pulsar C++ client library. That builds the Python binding for the library.

To install the built Python bindings:

```shell script
$ git clone https://github.com/apache/pulsar
$ cd pulsar/pulsar-client-cpp/python
$ sudo python setup.py install
```

# Example

In this example, the producer publishes data to the `my-topic` in your Pulsar cluster.
The content of each message payload is a combination of `hello-` and a digital (0-9) (e.g: `hello-0`).
The consumer receives the message from the `my-topic` and `acknowledges` each received message.

1. Run the consumer, and start to receiving the message from `my-topic`:

```bash
$ cd cloud/python
$ export SERVICE_URL="pulsar+ssl://cloud.streamnative.dev:6651"
$ export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
$ python SampleConsumer.py
```

Output:

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

2. Run the producer and publish messages to the `my-topic`:

```bash
$ cd cloud/python
$ export SERVICE_URL="pulsar+ssl://cloud.streamnative.dev:6651"
$ export AUTH_PARAMS="abcdefghijklmnopqretuiwxyz0123456789"
$ python SampleProducer.py
```

Output:

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
