# Idempotent Producer Example

This example demonstrates how to produce messages exactly-once using idempotent producers.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.3.0 or higher

## Examples

- [Idempotent Producer](../src/main/java/io/streamnative/examples/pubsub/IdempotentProducerExample.java)
- [Sync String Consumer](../src/main/java/io/streamnative/examples/pubsub/SyncStringConsumerExample.java)

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
   mvn -am -pl pubsub clean package
   ```

3. Create a namespace and configure it to enable message de-duplication.
   ```bash
   bin/pulsar-admin namespaces create public/idempotent
   ```
   ```bash
   bin/pulsar-admin namespaces set-deduplication --enable public/idempotent
   ```


4. Run the producer example to produce 10 avro messages to a pulsar topic `avro-payments`.
   ```bash
   mvn -pl schema exec:java -Dexec.mainClass="io.streamnative.examples.schema.avro.AvroSchemaProducerExample"
   ```
   After running this producer example, you will see the following successful message.
   ```bash
   Successfully produced 10 messages to a topic called avro-payments
   ```

4. Run the auto consumer example to receive the produced avro messages as generic records from topic `avro-payments`.
   ```bash
   mvn -pl schema exec:java -Dexec.mainClass="io.streamnative.examples.schema.generic.AutoConsumeSchemaConsumerExample"
   ```
   After running this consumer example, you will see the following output.
   ```bash
   key = id-0, value = {"id": "id-0", "amount": 0.000000}
   key = id-1, value = {"id": "id-1", "amount": 1000.000000}
   key = id-2, value = {"id": "id-2", "amount": 2000.000000}
   key = id-3, value = {"id": "id-3", "amount": 3000.000000}
   key = id-4, value = {"id": "id-4", "amount": 4000.000000}
   key = id-5, value = {"id": "id-5", "amount": 5000.000000}
   key = id-6, value = {"id": "id-6", "amount": 6000.000000}
   key = id-7, value = {"id": "id-7", "amount": 7000.000000}
   key = id-8, value = {"id": "id-8", "amount": 8000.000000}
   key = id-9, value = {"id": "id-9", "amount": 9000.000000}
    
   ```
   Then you can press "Ctrl+C" to stop the consumer example.
