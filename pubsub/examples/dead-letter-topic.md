# Dead Letter Topic Example

This example demonstrates how to use dead letter topic.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.4.0 or higher

## Examples

- [Dead Letter Topic Producer](../src/main/java/io/streamnative/examples/pubsub/DeadLetterTopicProducerExample.java)
- [Dead Letter Topic Consumer](../src/main/java/io/streamnative/examples/pubsub/DeadLetterTopicConsumerExample.java)

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
   bin/pulsar-admin namespaces create public/deadLetterTopic
   ```

4. Run the consumer example to wait for receiving the produced message from topic `public/deadLetterTopic/deadLetterTopicMessage`
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DeadLetterTopicConsumerExample" \
       -Dexec.args="-t public/deadLetterTopic/deadLetterTopicMessage -sn test-sub -st Shared -n 5"
   ```

5. Open another terminal, run the producer example to produce 5 messages to the topic `public/deadLetterTopic/deadLetterTopicMessage`.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DeadLetterTopicProducerExample" \
       -Dexec.args="-t public/deadLetterTopic/deadLetterTopicMessage -n 5"
   ```

6. Go to the terminal running the consumer example, you will see the following output.
```
Consumer Received message : value-0 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-1 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-2 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-3 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-4 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-0 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-1 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-2 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-3 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-4 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-0 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-1 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-2 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-3 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-4 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-0 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-1 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-2 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-3 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
Consumer Received message : value-4 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage
DeadLetterTopicConsumer Received message : value-0 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage-d
DeadLetterTopicConsumer Received message : value-1 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage-d
DeadLetterTopicConsumer Received message : value-2 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage-d
DeadLetterTopicConsumer Received message : value-3 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage-d
DeadLetterTopicConsumer Received message : value-4 Topic : persistent://public/deadLetterTopic/deadLetterTopicMessage-d


```
