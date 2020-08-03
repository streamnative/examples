# Overview

Produce message to and consume message from a Pulsar cluster using [Apache pulsar-cpp-client](https://github.com/apache/pulsar/tree/master/pulsar-client-cpp).

# Prerequisites



# Example

In this example, the producer will publish data to the `topic-1` in your Pulsar cluster.
The content of each message payload is  `content`.
The consumer will receive the message from the `topic-1` and `ack` the receipt of each message received.

1. Run the consumer, and start to receiving the message from `topic-1`:

```bash
$ cmake . && make && make install
$ ./SampleConsumer
```

Output:

```text
Received message with payload 'content'
```

2. Run the producer and publish messages to the `topic-1`:

```bash
$ cmake . && make && make install
$ ./SampleProducer
```

Output:

```text
Message sent: OK
```
