# Single Partition Atomic Producer Example

This example demonstrates how to produce multiple messages atomically to one topic (partition) using idempotent producers.

In order to achive that, you have to do the followings:

- Configure the topic to enable de-duplication.
- Enable idempotent producer by sending messages with incremental sequence ids.
- Enable batching in a way that the producer can control how to batch the messages and when to flush the batch.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.3.0 or higher

## Examples

- [Single Partition Atomic Producer](../src/main/java/io/streamnative/examples/pubsub/SinglePartitionAtomicProducerExample.java)
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

4. Run the consumer example to wait for receiving the produced message from topic `public/idempotent/atomic-producer-example`
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.SyncStringConsumerExample" \
       -Dexec.args="-t public/idempotent/atomic-producer-example -sn test-sub -st Exclusive -n 21"
   ```

5. Open another terminal, run the producer example to produce 21 messages to the topic `public/idempotent/atomic-producer-example`.
   The producer example will produce 1 message to establish the connection with broker, produce first 10 messages with sequence id
   from 1 to 10 in one message batch and produce another 10 messages with duplicated sequence id in other message batch, then another
   10 messages with sequence id from 11 to 20 in one message batch.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.SinglePartitionAtomicProducerExample" \
       -Dexec.args="-t public/idempotent/atomic-producer-example -n 10"
   ```

6. Go to the terminal running the consumer example, you will see the following output. The consumer example successfully received
   21 messages. The 10 messages produced with duplicated sequence id are de-duplicated.
   ```bash
Received message : value = 'value-0', sequence = 0
Received message : value = 'value-1', sequence = 1
Received message : value = 'value-2', sequence = 1
Received message : value = 'value-3', sequence = 1
Received message : value = 'value-4', sequence = 1
Received message : value = 'value-5', sequence = 1
Received message : value = 'value-6', sequence = 1
Received message : value = 'value-7', sequence = 1
Received message : value = 'value-8', sequence = 1
Received message : value = 'value-9', sequence = 1
Received message : value = 'value-10', sequence = 1
Received message : value = 'value-11', sequence = 11
Received message : value = 'value-12', sequence = 11
Received message : value = 'value-13', sequence = 11
Received message : value = 'value-14', sequence = 11
Received message : value = 'value-15', sequence = 11
Received message : value = 'value-16', sequence = 11
Received message : value = 'value-17', sequence = 11
Received message : value = 'value-18', sequence = 11
Received message : value = 'value-19', sequence = 11
Received message : value = 'value-20', sequence = 11
Successfully received 21 messages
   ```
