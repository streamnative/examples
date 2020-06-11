## Ack Individual

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic ack-individual-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Shared \
    --num-messages 0 \
    --ack-every-n-messages 2 \
    --ack-type Individual
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic ack-individual-example \
    --num-messages 10
```

```bash
bin/pulsar-admin topics stats-internal ack-individual-example
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic ack-individual-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Shared \
    --num-messages 0 \
    --ack-every-n-messages 1 \
    --ack-type Individual
```

## Ack Cumulative

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic ack-cumulative-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Exclusive \
    --num-messages 0 \
    --ack-every-n-messages 2 \
    --ack-type Cumulative
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic ack-cumulative-example \
    --num-messages 10
```

```bash
bin/pulsar-admin topics stats-internal ack-cumulative-example
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic ack-cumulative-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Exclusive \
    --num-messages 0 \
    --ack-every-n-messages 1 \
    --ack-type Cumulative
```

