# Overview

The `pulsar-client` and `pulsar-perf` are CLI tool written in Java language for the Apache Pulsar project.

# Prerequisites

- Pulsar client 2.6.1 or higher version (only for connecting the Pulsar cluster through the OAuth2 authentication plugin).

# Example

This section describes how to connect to a Pulsar cluster through the Oauth2 authentication plugin or the Token authentication plugin.

## pulsar-client

### Connect to cluster through OAuth2 authentication plugin

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Connect to the Pulsar cluster through the Oauth2 authentication plugin.

    ```shell script
    bin/pulsar-client \
      --url SERVICE_URL \
      --auth-plugin org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2 \
      --auth-params '{"privateKey":"file:///path/to/key/file.json",
        "issuerUrl":"https://test.auth0.com",
        "audience":"urn:sn:pulsar:test-pulsar-instance-namespace:test-pulsar-instance"}' \
      produce test-topic -m "test-message" -n 10
    ```

    **Output**:

    ```text
    ...
    12:21:50.920 [main] INFO  org.apache.pulsar.client.cli.PulsarClientTool - 10 messages successfully produced
    ```

### Connect to cluster through Token authentication plugin

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Token authentication parameters. For details, see [Get Token authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-token-authentication-parameters).

3. Connect to the Pulsar cluster through the Token authentication plugin.

    ```shell script
    ./bin/pulsar-client \
        --url SERVICE_URL \
        --auth-plugin org.apache.pulsar.client.impl.auth.AuthenticationToken \
        --auth-params token:AUTH_PARAMS \
        produce test-topic -m "test-message" -n 10
    ```

    **Output**:

    ```text
    ...
    12:21:50.920 [main] INFO  org.apache.pulsar.client.cli.PulsarClientTool - 10 messages successfully produced
    ```

## pulsar-perf

### Connect to cluster through OAuth2 authentication plugin

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Connect to the Pulsar cluster through the Oauth2 authentication plugin.

    ```shell script
    bin/pulsar-perf produce --service-url pulsar+ssl://cloud.streamnative.dev:6651 \
      --auth_plugin org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2 \
      --auth-params '{"privateKey":"file:///path/to/key/file.json",
        "issuerUrl":"https://test.auth0.com",
        "audience":"urn:sn:pulsar:test-pulsar-instance-namespace:test-pulsar-instance"}' \
      -r 1000 -s 1024 test-topic
    ```

    **Output**:

    ```text
    ...
    12:12:39.350 [pulsar-client-io-2-8] INFO  org.apache.pulsar.client.impl.PartitionedProducerImpl - [test-topic] Created partitioned producer
    12:12:39.351 [pulsar-perf-producer-exec-1-1] INFO  org.apache.pulsar.testclient.PerformanceProducer - Created 1 producers
    12:12:39.379 [pulsar-timer-7-1] WARN  com.scurrilous.circe.checksum.Crc32cIntChecksum - Failed to load Circe JNI library. Falling back to Java based CRC32c provider
    12:12:39.784 [main] INFO  org.apache.pulsar.testclient.PerformanceProducer - Throughput produced:      0.1  msg/s ---      0.0 Mbit/s --- failure      0.0 msg/s --- Latency: mean:   0.000 ms - med:   0.000 - 95pct:   0.000 - 99pct:   0.000 - 99.9pct:   0.000 - 99.99pct:   0.000 - Max:   0.000
    12:12:49.854 [main] INFO  org.apache.pulsar.testclient.PerformanceProducer - Throughput produced:    191.4  msg/s ---      1.5 Mbit/s --- failure      0.0 msg/s --- Latency: mean: 4647.764 ms - med: 5022.815 - 95pct: 7667.007 - 99pct: 7740.223 - 99.9pct: 7755.263 - 99.99pct: 7756.639 - Max: 7756.639
    12:12:59.894 [main] INFO  org.apache.pulsar.testclient.PerformanceProducer - Throughput produced:    900.4  msg/s ---      7.0 Mbit/s --- failure      0.0 msg/s --- Latency: mean: 1148.746 ms - med: 1093.679 -
    ...
    ```

### Connect to cluster through Token authentication plugin

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Token authentication parameters. For details, see [Get Token authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-token-authentication-parameters).

3. Connect to the Pulsar cluster through the Token authentication plugin.

    ```shell script
    ./bin/pulsar-perf produce \
        --service-url SERVICE_URL \
        --auth_plugin org.apache.pulsar.client.impl.auth.AuthenticationToken \
        --auth-params token:AUTH_PARAMS \
        -r 1000 -s 1024 test-topic
    ```

    **Output**:

    ```text
    ...
    12:12:39.350 [pulsar-client-io-2-8] INFO  org.apache.pulsar.client.impl.PartitionedProducerImpl - [test-topic] Created partitioned producer
    12:12:39.351 [pulsar-perf-producer-exec-1-1] INFO  org.apache.pulsar.testclient.PerformanceProducer - Created 1 producers
    12:12:39.379 [pulsar-timer-7-1] WARN  com.scurrilous.circe.checksum.Crc32cIntChecksum - Failed to load Circe JNI library. Falling back to Java based CRC32c provider
    12:12:39.784 [main] INFO  org.apache.pulsar.testclient.PerformanceProducer - Throughput produced:      0.1  msg/s ---      0.0 Mbit/s --- failure      0.0 msg/s --- Latency: mean:   0.000 ms - med:   0.000 - 95pct:   0.000 - 99pct:   0.000 - 99.9pct:   0.000 - 99.99pct:   0.000 - Max:   0.000
    12:12:49.854 [main] INFO  org.apache.pulsar.testclient.PerformanceProducer - Throughput produced:    191.4  msg/s ---      1.5 Mbit/s --- failure      0.0 msg/s --- Latency: mean: 4647.764 ms - med: 5022.815 - 95pct: 7667.007 - 99pct: 7740.223 - 99.9pct: 7755.263 - 99.99pct: 7756.639 - Max: 7756.639
    12:12:59.894 [main] INFO  org.apache.pulsar.testclient.PerformanceProducer - Throughput produced:    900.4  msg/s ---      7.0 Mbit/s --- failure      0.0 msg/s --- Latency: mean: 1148.746 ms - med: 1093.679 -
    ...
    ```