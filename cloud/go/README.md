# Overview

This document describes how to produce message to and consume message from a Pulsar cluster using the [Go client](https://github.com/apache/pulsar-client-go).

# Prerequisites

- Go 1.11 or higher version
- Go client 0.1.1+ (without 0.1.1)

If you have not installed Go, install it according to the [installation instruction](http://golang.org/doc/install).

# Example

**Note**

> This example shows how to connect to a Pulsar cluster through OAuth2. To connect to a Pulsar cluster through the token, see the implementation of [connectByToken.go](https://github.com/streamnative/pulsar-examples/blob/master/cloud/go/connectByToken.go).

In this example, the Go producer publishes data to the `topic-1` in your Pulsar cluster. The consumer receives the message from the `topic-1` and acknowledges each received message.
The content of each message payload is a combination of `hello-` and a digital (0-9), such as `hello-0`).

1. Get the Oauth2 options. For details, see [Get Oauth2 options](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-options).

2. Get the service URLs. For details, see [Get service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-service-urls).

3. Run the Go consumer to receive messages from topic `topic-1`.

       ```bash
       go build -o consumer sampleConsumer.go
       ./consumer -serviceURL pulsar+ssl://cloud.streamnative.dev:6651 \
              -privateKeyFile /path/to/private/key/file.txt\
              -audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name\
              -issuerUrl https://cloud.streamnative.dev\
              -clientId abcdefghigk0123456789
       ```
       Replace the Oauth2 options (`privateKeyFile`, `audience`, `issuerUrl`, and `clientId`) and the `-serviceURL` with the values you get from Step 1 and Step 2 respectively.

       **Output:**

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
       Received message msgId: {{10 26 0 0} <nil> 0xc0000e0160 {13817980335716792128 17177978 0x4cf4080}} -- content: 'hello-6'
       ```

4. Run the Go producer and publish messages to the `topic-1`.

       ```bash
       go build -o producer sampleProdcer.go
       ./producer -serviceURL pulsar+ssl://cloud.streamnative.dev:6651 \
              -privateKeyFile /path/to/private/key/file.txt\
              -audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name\
              -issuerUrl https://cloud.streamnative.dev\
              -clientId abcdefghigk0123456789
       ```

       Replace the Oauth2 options (`privateKeyFile`, `audience`, `issuerUrl`, and `clientId`) and the `-serviceURL` with the values you get from Step 1 and Step 2 respectively.

       **Output:**

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