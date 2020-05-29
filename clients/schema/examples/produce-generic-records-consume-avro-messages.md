# Java Producer using generic schema and Consumer using Avro schema

This example demonstrates how to produce messages to Apache Pulsar using generic schema, and
how to consume messages from Apache Pulsar using [Avro](http://avro.apache.org) schema.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.3.0 or higher

## Examples

- [Java Generic Schema Producer](../src/main/java/io/streamnative/examples/schema/generic/GenericSchemaProducerExample.java)
- [Java Avro Consumer](../src/main/java/io/streamnative/examples/schema/avro/AvroSchemaConsumerExample.java)

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
   cd pulsar-examples
   ```
   ```bash
   mvn -am -pl schema clean package
   ```

3. Run the Generic schema producer example to produce 10 generic records to a pulsar topic `avro-payments`.
   ```bash
   mvn -pl schema exec:java -Dexec.mainClass="io.streamnative.examples.schema.generic.GenericSchemaProducerExample"
   ```
   After running this producer example, you will see the following successful message.
   ```bash
   Successfully produced 10 messages to a topic called avro-payments
   ```

4. Run the Avro consumer example to receive the produced generic records from topic `avro-payments`.
   ```bash
   mvn -pl schema exec:java -Dexec.mainClass="io.streamnative.examples.schema.avro.AvroSchemaConsumerExample"
   ```
   After running this consumer example, you will see the following output.
   ```bash
   key = id-0, value = {"id": "id-0", "amount": 0.0}
   key = id-1, value = {"id": "id-1", "amount": 1000.0}
   key = id-2, value = {"id": "id-2", "amount": 2000.0}
   key = id-3, value = {"id": "id-3", "amount": 3000.0}
   key = id-4, value = {"id": "id-4", "amount": 4000.0}
   key = id-5, value = {"id": "id-5", "amount": 5000.0}
   key = id-6, value = {"id": "id-6", "amount": 6000.0}
   key = id-7, value = {"id": "id-7", "amount": 7000.0}
   key = id-8, value = {"id": "id-8", "amount": 8000.0}
   key = id-9, value = {"id": "id-9", "amount": 9000.0}
   ```
   Then you can press "Ctrl+C" to stop the consumer example.
