# Dead Letter Topic Example

This example demonstrates how to use dead letter topic.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.4.0 or higher

## Examples

- [Sync String Producer](../src/main/java/io/streamnative/examples/pubsub/SyncStringProducerExample.java)

It will publish 10 messages.
- [Dead Letter Topic Consumer](../src/main/java/io/streamnative/examples/pubsub/DeadLetterTopicConsumerExample.java)

It will looping consume messages and set redeliverCount to 3. For even numbers, process the message and ack it. For odd numbers, skip ack. When the number of redeliver reached redeliverCount times, DLT will be triggered.
- [Sync String Producer](../src/main/java/io/streamnative/examples/pubsub/SyncStringProducerExample.java)

It will consume messages from specified dead letter topic.

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

3. Create a namespace.
   ```bash
   bin/pulsar-admin namespaces create public/dlt-example
   ```

4. Run the dead letter topic consumer example to wait for receiving the produced message from topic `public/dlt-example/dlt-example-topic`.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DeadLetterTopicConsumerExample" \
       -Dexec.args="-t public/dlt-example/dlt-example-topic -sn test-sub -st Shared -n 0"
   ```
5. Open another terminal, run the consumer example to wait for receiving the  dead letter topic message from topic `public/dlt-example/dlt-example-topic-dlt`.
```bash
   mvn -pl pubsub exec:java \  
   -Dexec.mainClass="io.streamnative.examples.pubsub.SyncStringConsumerExample" \
   -Dexec.args="-t persistent://public/dlt-example/dlt-example-topic-dlt -sn test-sub -st Shared -n 5"
   ```

6. Open another terminal, run the producer example to produce 10 messages to the topic `public/dlt-example/dlt-example-topic`.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.SyncStringProducerExample" \
       -Dexec.args="-t public/dlt-example/dlt-example-topic -n 10"
   ```

7. Go to the terminal running the dead letter topic consumer example, you will see the following output. Messages not ack were redelivered 3 times and others were not redelivered.
```bash
Consumer Received message : value-0; Send ack
Consumer Received message : value-1; Don't send ack
Consumer Received message : value-2; Send ack
Consumer Received message : value-3; Don't send ack
Consumer Received message : value-4; Send ack
Consumer Received message : value-5; Don't send ack
Consumer Received message : value-6; Send ack
Consumer Received message : value-7; Don't send ack
Consumer Received message : value-8; Send ack
Consumer Received message : value-9; Don't send ack
Consumer Received message : value-1; Don't send ack
Consumer Received message : value-3; Don't send ack
Consumer Received message : value-5; Don't send ack
Consumer Received message : value-7; Don't send ack
Consumer Received message : value-9; Don't send ack
Consumer Received message : value-1; Don't send ack
Consumer Received message : value-3; Don't send ack
Consumer Received message : value-5; Don't send ack
Consumer Received message : value-7; Don't send ack
Consumer Received message : value-9; Don't send ack
Consumer Received message : value-1; Don't send ack
Consumer Received message : value-3; Don't send ack
Consumer Received message : value-5; Don't send ack
Consumer Received message : value-7; Don't send ack
Consumer Received message : value-9; Don't send ack
```
8. Go to the terminal running the consumer example reciving messages from dead letter topic, you will see the following output. Messages not ack were delivered to dead letter topic.
```bash
Received message : value = 'value-1', sequence = 0
Received message : value = 'value-3', sequence = 1
Received message : value = 'value-5', sequence = 2
Received message : value = 'value-7', sequence = 3
Received message : value = 'value-9', sequence = 4
Successfully received 5 messages
```