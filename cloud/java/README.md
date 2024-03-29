# Overview

This document describes how to produce messages to and consume messages from a Apache Pulsar cluster using the Java Producer and Consumer API.

# Prerequisites

- Java 1.8 or higher version
- Java Client: 2.6.1+

# Example

**Note**

> This example shows how to connect to a Pulsar cluster through the OAuth2 authentication plugin. To connect to a Pulsar cluster through the Token authentication plugin, see the implementation of [connectByToken.go](https://github.com/streamnative/pulsar-examples/blob/master/cloud/java/src/main/java/io/streamnative/examples/oauth2/ConnectByToken.java).

In this example, the Java producer publishes messages to the `topic-1` in your Pulsar cluster. The consumer receives the message from the `topic-1` and acknowledges each received message.
The content of each message payload is a combination of `my-message-` and a digital (0-9) (e.g: `my-message-0`).

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Run the Java consumer to receive messages from the topic `topic-1`.

      ```shell script
      # Compile the Java code
      mvn clean package

      # Run the consumer
      mvn exec:java -Dexec.mainClass="io.streamnative.examples.oauth2.SampleConsumer" \
          -Dexec.args="--serviceUrl pulsar+ssl://streamnative.cloud:6651 --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name --issuerUrl https://streamnative.cloud --privateKey file:///path/to/private/key/file.txt"
      ```

      **Output**:

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

4. Run the Java producer to publish messages to the topic `topic-1`.

      ```shell script
      # Compile the Java code
      mvn clean package

      # Run the producer
      mvn exec:java -Dexec.mainClass="io.streamnative.examples.oauth2.SampleConsumer" \
          -Dexec.args="--serviceUrl pulsar+ssl://streamnative.cloud:6651 --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name --issuerUrl https://streamnative.cloud --privateKey file:///path/to/private/key/file.txt"
      ```

      **Output**:

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
   
5. Run the Java consumer by use clientId and clientSecret to receive messages from the topic `topic-1`.(Optional)

      ```shell script
      # Compile the Java code
      mvn clean package
    
      # Run the consumer
      mvn exec:java -Dexec.mainClass="io.streamnative.examples.oauth2.SampleConsumerNoCredentialFile" \
                -Dexec.args="--serviceUrl your-serviceUrl --audience your-audience  --scope your-scope --issuerUrl your-issuer-url --clientId your-client-id --clientSecret your-client-secret"
      ```
   
      ```text
       Receive message my-message-0
       Receive message my-message-1
       Receive message my-message-2
       Receive message my-message-3
       Receive message my-message-4
       Receive message my-message-5
       Receive message my-message-6
       Receive message my-message-7
       Receive message my-message-8
       Receive message my-message-9
      ```
   
4. Run the Java producer to publish messages to the topic `topic-1`.(Optional)

      ```shell script
      # Compile the Java code
      mvn clean package

      # Run the producer
      mvn exec:java -Dexec.mainClass="io.streamnative.examples.oauth2.SampleProducerNoCredentialFile" \
          -Dexec.args="--serviceUrl your-serviceUrl --audience your-audience  --scope your-scope --issuerUrl your-issuer-url --clientId your-client-id --clientSecret your-client-secret"
      ```
   
      ```text
       Publish my-message-0 and message ID 25:0:0:0
       Publish my-message-1 and message ID 65:0:0:0
       Publish my-message-2 and message ID 65:1:0:0
       Publish my-message-3 and message ID 65:2:0:0
       Publish my-message-4 and message ID 65:3:0:0
       Publish my-message-5 and message ID 65:4:0:0
       Publish my-message-6 and message ID 65:5:0:0
       Publish my-message-7 and message ID 65:6:0:0
       Publish my-message-8 and message ID 65:7:0:0
       Publish my-message-9 and message ID 65:8:0:0
      ```