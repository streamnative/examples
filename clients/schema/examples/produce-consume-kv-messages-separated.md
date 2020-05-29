# Java Producer and Consumer using KeyValue schema

> The example is using `SEPARATED` key/value schema which stores key as the message key and value as the message payload.

This example demonstrates how to produce key/value messages to and key/value consume messages from Apache Pulsar
using KeyValue schema.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.3.0 or higher

## Examples

- [Java KeyValue Producer](../src/main/java/io/streamnative/examples/schema/kv/KeyValueSeparatedSchemaProducerExample.java)
- [Java KeyValue Consumer](../src/main/java/io/streamnative/examples/schema/kv/KeyValueSeparatedSchemaConsumerExample.java)

## Steps

1. Start Pulsar standalone. You can follow the [detailed instructions](http://pulsar.apache.org/docs/en/next/standalone/)
in Pulsar documentation to start a Pulsar standalone locally.
   ```bash
   bin/pulsar standalone
   ```

2. Clone the examples repo and build the schema examples.
   ```bash
   git clone https://github.com/streamnative/pulsar-examples.git
   ```
   ```bash
   cd pulsar-examples/clients
   ```
   ```bash
   mvn -am -pl schema clean package
   ```

3. Run the consumer example to wait for receiving the produced key/value messages from topic `keyvalue-separated-topic`.
   ```bash
   java -cp schema/target/pulsar-schema-examples.jar io.streamnative.examples.schema.kv.KeyValueSeparatedSchemaConsumerExample
   ```
   
4. Open another terminal, run the producer example to produce 10 key/value messages to a pulsar topic `keyvalue-separated-topic`.
   ```bash
   java -cp schema/target/pulsar-schema-examples.jar io.streamnative.examples.schema.kv.KeyValueSeparatedSchemaProducerExample
   ```
   After running this producer example, you will see the following successful message.
   ```bash
   Successfully produced 10 messages to a topic called keyvalue-separated-topic
   ```

5. Go to the terminal running the consumer example, you will see the following output.
   ```bash
   key = 0, value = value-0
   key = 1, value = value-1
   key = 2, value = value-2
   key = 3, value = value-3
   key = 4, value = value-4
   key = 5, value = value-5
   key = 6, value = value-6
   key = 7, value = value-7
   key = 8, value = value-8
   key = 9, value = value-9
   ```
   Then you can press "Ctrl+C" to stop the consumer example.
