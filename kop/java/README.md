# Overview

This document describes how to produce messages to and consume messages from a KoP cluster using the Kafka client.

# Prerequisites

- Java 1.8 or higher version
- Maven

> **NOTE**
>
> This example uses Kafka client 2.0.0. If you want to use another version of Kafka client, you can change the `kafka.version` property in `pom.xml` file. Kafka client version 1.0.0 - 2.6.0 are supported.

# Example

See [KoP Security](https://github.com/streamnative/kop/blob/master/docs/security.md) for how to configure KoP with token authentication. This example takes a topic named `my-topic` under `public/default` namespace as reference.

1. Grant produce and consume permissions to the specific role.

   ```bash
   bin/pulsar-admin namespaces grant-permission public/default \
     --role <role> \
     --actions produce,consume
   ```

   > **NOTE**
   >
   > The `conf/client.conf` should be configured. For details, see [Configure CLI Tools](http://pulsar.apache.org/docs/en/security-jwt/#cli-tools).

2. Configure the token in [token.properties](src/main/resources/token.properties).

   ```properties
   topic=persistent://public/default/my-topic
   namespace=public/default
   token=token:<token-of-the-role>
   ```

3. Compile the project.

   ```
   mvn clean compile
   ```

4. Run a Kafka producer to produce a `hello` message.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.TokenProducer
   ```

   **Output:**

   ```
   Send hello to persistent://public/default/my-topic-0@0
   ```

5. Run a Kafka consumer to consume some messages.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.TokenConsumer
   ```

   **Output:**

   ```
   Receive record: hello from persistent://public/default/my-topic-0@0
   ```
