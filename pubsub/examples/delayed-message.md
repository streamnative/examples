# Idempotent Producer Example

This example demonstrates how to produce messages exactly-once using idempotent producers.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.4.0 or higher

## Examples

- [Delayed Message Producer](../src/main/java/io/streamnative/examples/pubsub/DelayedMessageProducerExample.java)
- [Delayed Message Consumer](../src/main/java/io/streamnative/examples/pubsub/DelayedMessageConsumerExample.java)

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
   bin/pulsar-admin namespaces create public/delayed
   ```

4. Run the consumer example to wait for receiving the produced message from topic `public/delayed/delayed-messages`
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DelayedMessageConsumerExample" \
       -Dexec.args="-t public/delayed/delayed-messages -sn test-sub -st Shared -n 15"
   ```

5. Open another terminal, run the producer example to produce 15 messages to the topic `public/delayed/delayed-messages`.
   The producer example will produce the first 5 messages immediately, produce 5 messages delayed 5 seconds using `deliverAfter` and produce 5 messages delayed 10 seconds using `deliverAt`.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DelayedMessageProducerExample" \
       -Dexec.args="-t public/delayed/delayed-messages -n 5"
   ```

6. Go to the terminal running the consumer example, you will see the following output. The consumer example successfully received
   15 messages. They are consumed according to the prescribed time.
```
Consumer Received message : Immediate delivery message 0; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:26 CST 2019
Consumer Received message : Immediate delivery message 1; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:26 CST 2019
Consumer Received message : Immediate delivery message 2; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:26 CST 2019
Consumer Received message : Immediate delivery message 3; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:26 CST 2019
Consumer Received message : Immediate delivery message 4; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:26 CST 2019
Consumer Received message : DeliverAfter 5 seconds message 0; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:30 CST 2019
Consumer Received message : DeliverAfter 5 seconds message 1; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:30 CST 2019
Consumer Received message : DeliverAfter 5 seconds message 2; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:30 CST 2019
Consumer Received message : DeliverAfter 5 seconds message 3; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:30 CST 2019
Consumer Received message : DeliverAfter 5 seconds message 4; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:30 CST 2019
Consumer Received message : DeliverAt 10 seconds message 0; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:35 CST 2019
Consumer Received message : DeliverAt 10 seconds message 1; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:35 CST 2019
Consumer Received message : DeliverAt 10 seconds message 2; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:35 CST 2019
Consumer Received message : DeliverAt 10 seconds message 3; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:35 CST 2019
Consumer Received message : DeliverAt 10 seconds message 4; produce-time = Thu Jul 04 12:31:26 CST 2019; receive-time = Thu Jul 04 12:31:35 CST 2019
Successfully received 15 messages

```
