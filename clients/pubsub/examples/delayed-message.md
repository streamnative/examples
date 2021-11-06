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

- [Delayed Message Producer With Message Router](../src/main/java/io/streamnative/examples/pubsub/DelayedMessageProducerWithMessageRouterExample.java)

It will publish 10 message immediately and publish another 10 messages delayed 0 ~ 90 seconds, the similar delay time will put same partition. Such as:

Delayed message of 0 seconds ~ 30 seconds will be put in partition 0.
Delayed message of 30 seconds ~ 1 minutes will be put in partition 1.
Delayed message of 1 minutes ~ 10 minutes will be put in partition 2.
Delayed message of 10 minutes ~ 1 hours will be put in partition 3.
Delayed message of 1 hours ~ 12 hours will be put in partition 4.
Delayed message of 12 hours ~ 1 days will be put in partition 5.
Delayed message of 1 days ~ 3 days will be put in partition 6.
Delayed message of 3 days ~ 7 days will be put in partition 7.
Delayed message delay time after 7 days will be put in partition 8.

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
   cd pulsar-examples/clients
   ```
   ```bash
   mvn clean install -DskipTests
   ```

3. Create a namespace.
   ```bash
   bin/pulsar-admin namespaces create public/delayed-delivery-example
   ```

4. Run the consumer example to wait for receiving the produced message from topic `public/delayed-delivery-example/delayed-delivery-example-topic`
   ```bash
   java -cp pubsub/target/pulsar-pubsub-examples.jar \
     io.streamnative.examples.pubsub.DelayedMessageConsumerExample \
     -t public/delayed-delivery-example/delayed-delivery-example-topic \
     -sn test-sub \
     -st Shared \
     -n 0
   ```

5. Open another terminal, run the DelayedAfterMessageProducer example to produce 10 messages to the topic `public/delayed-delivery-example/delayed-delivery-example-topic`.
   The producer example will produce the first 5 messages immediately and produce 5 messages delayed 5 seconds using `deliverAfter`.
   ```bash
   java -cp pubsub/target/pulsar-pubsub-examples.jar \
     io.streamnative.examples.pubsub.DelayedAfterMessageProducerExample \
     -t public/delayed-delivery-example/delayed-delivery-example-topic \
     -n 5
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
   java -cp pubsub/target/pulsar-pubsub-examples.jar \
     io.streamnative.examples.pubsub.DelayedAtMessageProducerExample \
     -t public/delayed-delivery-example/delayed-delivery-example-topic -n 5
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

9. Create a topic has 9 partitions
   ```shell
   bin/pulsar-admin topics create-partitioned-topic persistent://public/delayed-delivery-example/delayed-message-producer-with-message-router-example-topic -p 9
   ```

10. Run the consumer example to wait for receiving the produced message from topic `public/delayed-delivery-example/delayed-message-producer-with-message-router-example-topic`
   ```shell
   java -cp pubsub/target/pulsar-pubsub-examples.jar \
     io.streamnative.examples.pubsub.DelayedMessageConsumerExample \
     -t public/delayed-delivery-example/delayed-message-producer-with-message-router-example-topic \
     -sn test-sub \
     -st Shared \
     -n 0 \
     -ppn
   ```

11. Open another terminal, run the DelayedMessageProducerWithMessageRouterExample example to produce 10 messages to the topic `public/delayed-delivery-example/delayed-message-producer-with-message-router-example-topic`.
    The producer example will produce the first 10 messages immediately and produce 10 messages delayed 0 ~ 90 seconds using a custom message router.
   ```shell
   java -cp pubsub/target/pulsar-pubsub-examples.jar \
     io.streamnative.examples.pubsub.DelayedMessageProducerWithMessageRouterExample \
     -t public/delayed-delivery-example/delayed-message-producer-with-message-router-example-topic -n 10
   ```

12. Go to the terminal running the consumer example, you will see the following output. The consumer example successfully received
    20 messages. For not using `deliverAfter`, the difference between publish time and receive time is 0 seconds. For using `deliverAfter`, the similar delay time will put same partition.
    ```shell
      Consumer Received message : Immediate delivery message 5, Partition : 1; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 3, Partition : 8; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 4, Partition : 0; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 7, Partition : 3; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 6, Partition : 2; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 2, Partition : 7; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 1, Partition : 6; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 8, Partition : 4; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 0, Partition : 5; Difference between publish time and receive time = 0 seconds
      Consumer Received message : Immediate delivery message 9, Partition : 5; Difference between publish time and receive time = 0 seconds
      Consumer Received message : DeliverAfter message 0, delay time : 0, Partition : 0; Difference between publish time and receive time = 0 seconds
      Consumer Received message : DeliverAfter message 1, delay time : 10, Partition : 0; Difference between publish time and receive time = 10 seconds
      Consumer Received message : DeliverAfter message 2, delay time : 20, Partition : 0; Difference between publish time and receive time = 20 seconds
      Consumer Received message : DeliverAfter message 3, delay time : 30, Partition : 0; Difference between publish time and receive time = 30 seconds
      Consumer Received message : DeliverAfter message 4, delay time : 40, Partition : 1; Difference between publish time and receive time = 40 seconds
      Consumer Received message : DeliverAfter message 5, delay time : 50, Partition : 1; Difference between publish time and receive time = 50 seconds
      Consumer Received message : DeliverAfter message 6, delay time : 60, Partition : 1; Difference between publish time and receive time = 60 seconds
      Consumer Received message : DeliverAfter message 7, delay time : 70, Partition : 2; Difference between publish time and receive time = 70 seconds
      Consumer Received message : DeliverAfter message 8, delay time : 80, Partition : 2; Difference between publish time and receive time = 80 seconds
      Consumer Received message : DeliverAfter message 9, delay time : 90, Partition : 2; Difference between publish time and receive time = 90 seconds

    ```