# Pulsar Flink Connector Examples

This is a curated list of examples that demonstrate how to process event streams in Apache Pulsar using Apache Flink.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.5.2 or higher
- Flink 1.10.1

## Pulsar Streaming Word Count

This example demonstrates a Flink streaming job that reads events from Pulsar, processes them and produces the word count results
back to Pulsar.

```bash
export INPUT_TOPIC=wordcount_input
export OUTPUT_TOPIC=wordcount_output
```

### Steps

1. Download Pulsar 2.5.1 and start Pulsar standalone. Assume `PULSAR_HOME` is the root directory of pulsar distribution.

    ```bash
    ${PULSAR_HOME}/bin/pulsar standalone
    ```

2. Download Flink 1.10.1 and start Flink locally. Assume `FLINK_HOME` is the root directory of flink distribution.

    ```bash
    ${FLINK_HOME}/bin/start-cluster.sh
    ```

3. Clone the examples repo and build the flink examples. Assume `EXAMPLES_HOME` is the root directory of the cloned `streamnative/pulsar-examples` repo.

    ```bash
    git clone https://github.com/streamnative/pulsar-examples.git
    ```

    ```bash
    cd pulsar-examples/pulsar-flink
    ```

    ```bash
    mvn clean install
    ```

4. Open a terminal to subscribe to the output topic `${OUTPUT_TOPIC}` to receive word count results from it.

    ```bash
    ${PULSAR_HOME}/bin/pulsar-client consume -s sub -n 0 ${OUTPUT_TOPIC}
    ```

5. Open a terminal to submit the PulsarStreamingWordCount job to Flink.

    ```bash
    ${FLINK_HOME}/bin/flink run ${EXAMPLES_HOME}/pulsar-flink/target/pulsar-flink-examples-0.0.0-SNAPSHOT.jar \
        --broker-service-url pulsar://localhost:6650 \
        --admin-service-url http://localhost:8080 \
        --input-topic ${INPUT_TOPIC} --output-topic ${OUTPUT_TOPIC}
    ```

6. Open a terminal to produce a stream of sentences to the input topic `${INPUT_TOPIC}`.

    ```bash
    ${PULSAR_HOME}/bin/pulsar-client produce -m "test flink streaming word count" -n 100 ${INPUT_TOPIC}
    ```

7. In the terminal of `step 4`, you should see a stream of wordcount results similar as below. The wordcount results are saved in AVRO format in the output topic.

    ```bash
    ----- got message -----
    test�
    ----- got message -----

    count�
    ----- got message -----
    word�
    ----- got message -----
    streaming�
    ----- got message -----

    flink�
    ```