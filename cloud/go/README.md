# Overview

This document describes how to produce message to and consume message from a Pulsar cluster using [Apache pulsar-client-go](https://github.com/apache/pulsar-client-go).

# Prerequisites

If you have not installed Go, install it according to the [installation instruction](http://golang.org/doc/install).

Since the `go mod` package management tool is used in this project, **Go 1.11 or higher** version is required.

- Go 1.11+
- pulsar-client-go 0.1.1+(not include 0.1.1)
- Get the `serviceURL` of your StreamNative Cloud Pulsar cluster: [How to get service URL](#How to get service URL)
- Get the OAuth2 `privateKeyFile` of a service account to access your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](#How to get OAuth2 options)
- Get the `audience` of the your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](#How to get OAuth2 options)
- Get the `issuerUrl` of the your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](#How to get OAuth2 options)
- Get the `clientId` of the your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](#How to get OAuth2 options)

# Example

In this example, the producer publishes data to the `topic-1` in your Pulsar cluster.
The content of each message payload is a combination of `hello-` and a digital (0-9) (e.g: `hello-0`).
The consumer receives the message from the `topic-1` and `acknowledges` each received message.

> Tips: The following code example uses the OAuth2 connection method. If you want to connect to the Pulsar cluster using Token, please refer to the implementation of **connectByToken.go**.

1. Run the consumer, and start to receiving the message from `topic-1`:

```bash
$ go build -o consumer sampleConsumer.go
$ ./consumer -serviceURL pulsar+ssl://cloud.streamnative.dev:6651 \
       -privateKeyFile /path/to/private/key/file.json\
       -audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name\
       -issuerUrl https://cloud.streamnative.dev\
       -clientId abcdefghigk0123456789
```

Output:

```text
Received message msgId: {{10 17 0 0} <nil> 0xc0000e0160 {13817980335716751128 17136978 0x4cf4080}} -- content: 'hello-7'
Received message msgId: {{10 18 0 0} <nil> 0xc0000e0160 {13817980335716772128 17157780 0x4cf4080}} -- content: 'hello-8'
Received message msgId: {{10 19 0 0} <nil> 0xc0000e0160 {13817980335716774128 17160202 0x4cf4080}} -- content: 'hello-9'
Received message msgId: {{10 20 0 0} <nil> 0xc0000e0160 {13817980335716776128 17162019 0x4cf4080}} -- content: 'hello-0'
Received message msgId: {{10 21 0 0} <nil> 0xc0000e0160 {13817980335716780128 17165615 0x4cf4080}} -- content: 'hello-1'
Received message msgId: {{10 22 0 0} <nil> 0xc0000e0160 {13817980335716781128 17167300 0x4cf4080}} -- content: 'hello-2'
Received message msgId: {{10 23 0 0} <nil> 0xc0000e0160 {13817980335716783128 17169197 0x4cf4080}} -- content: 'hello-3'
Received message msgId: {{10 24 0 0} <nil> 0xc0000e0160 {13817980335716788128 17174514 0x4cf4080}} -- content: 'hello-4'
Received message msgId: {{10 25 0 0} <nil> 0xc0000e0160 {13817980335716790128 17176145 0x4cf4080}} -- content: 'hello-5'
Received message msgId: {{10 26 0 0} <nil> 0xc0000e0160 {13817980335716792128 17177978 0x4cf4080}} -- content: 'hello-6'```
```

2. Run the producer and publish messages to the `topic-1`:

```bash
$ go build -o producer sampleProdcer.go
$ ./producer -serviceURL pulsar+ssl://cloud.streamnative.dev:6651 \
       -privateKeyFile /path/to/private/key/file.json\
       -audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name\
       -issuerUrl https://cloud.streamnative.dev\
       -clientId abcdefghigk0123456789
```

Output:

```text
Published message: {10 20 0 0} 
Published message: {10 21 0 0} 
Published message: {10 22 0 0} 
Published message: {10 23 0 0} 
Published message: {10 24 0 0} 
Published message: {10 25 0 0} 
Published message: {10 26 0 0} 
Published message: {10 27 0 0} 
Published message: {10 28 0 0} 
Published message: {10 29 0 0}
```
