# Overview

This document describes how to use transactions in cluster on a cluster with transaction enabled.

# Prerequisites

- Java 1.8 or higher version
- Java Client: 2.8.0+
- Maven

> **NOTE**
>
> This example uses Pulsar client 2.8.0. If you want to use another version of Pulsar client, you can change the `pulsar.version` property in `pom.xml` file.

# Example

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Run the Java transaction example.

      ```shell script
      # Compile the Java code
      mvn clean package

      # Run the example
      mvn exec:java -Dexec.mainClass="io.streamnative.examples.transaction.TransactionSyncExample" \
          -Dexec.args="--serviceUrl pulsar+ssl://streamnative.cloud:6651 --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name --issuerUrl https://streamnative.cloud --privateKey file:///path/to/private/key/file.txt"
      ```
      **Output**:

      ```text
      Receive transaction message: Hello Pulsar! count : 1
      Receive transaction message: Hello Pulsar! count : 2
      ```