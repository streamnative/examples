# Delayed message Example

This example demonstrates how to use delayed message delivery feature.

## Prerequisites

- Java 1.8 or higher to run the demo application
- Maven to compile the demo application
- Pulsar 2.4.0 or higher

## Examples

- [DelayedAfter Message Producer](../src/main/java/io/streamnative/examples/pubsub/DelayedAfterMessageProducerExample.java) 

It will publish 5 messages immediately and publish another 5 messages delayed 5 seconds by using `deliverAfter`.

- [DelayedAt Message Producer](../src/main/java/io/streamnative/examples/pubsub/DelayedAtMessageProducerExample.java)  

It will publish 5 messages immediately and publish another 5 messages delayed 5 seconds by using `deliverAt`.

- [Delayed Message Consumer](../src/main/java/io/streamnative/examples/pubsub/DelayedMessageConsumerExample.java)

It runs in a loop receiving messages.

- Expected Result

Consumer will receive no delayed messages immediately and receive delayed messages after 5 seconds.

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
   bin/pulsar-admin namespaces create public/delayed-delivery-example
   ```

4. Run the consumer example to wait for receiving the produced message from topic `public/delayed-delivery-example/delayed-delivery-example-topic`
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DelayedMessageConsumerExample" \
       -Dexec.args="-t public/delayed-delivery-example/delayed-delivery-example-topic -sn test-sub -st Shared -n 0"
   ```

5. Open another terminal, run the DelayedAfterMessageProducer example to produce 10 messages to the topic `public/delayed-delivery-example/delayed-delivery-example-topic`.
   The producer example will produce the first 5 messages immediately and produce 5 messages delayed 5 seconds using `deliverAfter`.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DelayedAfterMessageProducerExample" \
       -Dexec.args="-t public/delayed-delivery-example/delayed-delivery-example-topic -n 5"
   ```

6. Go to the terminal running the consumer example, you will see the following output. The consumer example successfully received
   10 messages. For not using `deliverAfter`, the difference between publish time and receive time is 0 seconds. For using `deliverAfter`, is 5 seconds.
```bash
Consumer Received message : Immediate delivery message 0; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 1; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 2; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 3; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 4; Difference between publish time and receive time = 0 seconds
Consumer Received message : DeliverAfter message 0; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAfter message 1; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAfter message 2; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAfter message 3; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAfter message 4; Difference between publish time and receive time = 5 seconds
```

7. Open another terminal, run the DelayedAtMessageProducer example to produce 10 messages to the topic `public/delayed-delivery-example/delayed-delivery-example-topic`.
   The producer example will produce the first 5 messages immediately and produce 5 messages delayed 10 seconds using `deliverAt`.
   ```bash
   mvn -pl pubsub exec:java \
       -Dexec.mainClass="io.streamnative.examples.pubsub.DelayedAtMessageProducerExample" \
       -Dexec.args="-t public/delayed-delivery-example/delayed-delivery-example-topic -n 5"
   ```

8. Go to the terminal running the consumer example, you will see the following output. The consumer example successfully received
   10 messages. For not using `deliverAt`, the difference between publish time and receive time is 0 seconds. For using `deliverAt`, is 5 seconds.
```bash
Consumer Received message : Immediate delivery message 0; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 1; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 2; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 3; Difference between publish time and receive time = 0 seconds
Consumer Received message : Immediate delivery message 4; Difference between publish time and receive time = 0 seconds
Consumer Received message : DeliverAt message 0; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAt message 1; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAt message 2; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAt message 3; Difference between publish time and receive time = 5 seconds
Consumer Received message : DeliverAt message 4; Difference between publish time and receive time = 5 seconds
```
