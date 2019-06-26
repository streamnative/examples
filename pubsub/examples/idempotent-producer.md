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

4. Run the consumer example to wait for receiving the produced message from topic `public/idempotent/idempotent-messages`
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.SyncStringConsumerExample" \
       -Dexec.args="-t public/idempotent/idempotent-messages -sn test-sub -st Exclusive -n 20"
   ```

5. Open another terminal, run the producer example to produce 20 messages to the topic `public/idempotent/idempotent-messages`.
   The producer example will produce the first 10 messages with sequence id from 0 to 9 and produce another 10 messages with
   duplicated sequence id, then another 10 messages with sequence id from 10 to 19.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.IdempotentProducerExample" \
       -Dexec.args="-t public/idempotent/idempotent-messages -n 10"
   ```

6. Go to the terminal running the consumer example, you will see the following output. The consumer example successfully received
   20 messages. The 10 messages produced with duplicated sequence id are de-duplicated.
   ```bash
Received message : value = 'value-0', sequence = 0
Received message : value = 'value-1', sequence = 1
Received message : value = 'value-2', sequence = 2
Received message : value = 'value-3', sequence = 3
Received message : value = 'value-4', sequence = 4
Received message : value = 'value-5', sequence = 5
Received message : value = 'value-6', sequence = 6
Received message : value = 'value-7', sequence = 7
Received message : value = 'value-8', sequence = 8
Received message : value = 'value-9', sequence = 9
Received message : value = 'value-10', sequence = 10
Received message : value = 'value-11', sequence = 11
Received message : value = 'value-12', sequence = 12
Received message : value = 'value-13', sequence = 13
Received message : value = 'value-14', sequence = 14
Received message : value = 'value-15', sequence = 15
Received message : value = 'value-16', sequence = 16
Received message : value = 'value-17', sequence = 17
Received message : value = 'value-18', sequence = 18
Received message : value = 'value-19', sequence = 19
Successfully received 20 messages
   ```
