## Exclusive

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-exclusive-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Exclusive \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic sub-exclusive-example \
    --num-messages 10
```

```bash
bin/pulsar-admin topics stats sub-exclusive-example
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-exclusive-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Exclusive \
    --num-messages 0
```

## Failover

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-failover-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Failover \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-failover-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Failover \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic sub-failover-example \
    --messages-rate 1 \
    --num-messages 100000000
```

```bash
bin/pulsar-admin topics stats sub-failover-example
```

## Shared

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-shared-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Shared \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-shared-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Shared \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic sub-shared-example \
    --num-keys 3 \
    --messages-rate 1 \
    --num-messages 100000000
```

```bash
bin/pulsar-admin topics stats sub-shared-example
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-shared-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Shared \
    --num-messages 0
```

## Key_Shared

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-key_shared-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Key_Shared \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-key_shared-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Key_Shared \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic sub-key_shared-example \
    --num-keys 3 \
    --messages-rate 1 \
    --num-messages 100000000
```

```bash
bin/pulsar-admin topics stats sub-key_shared-example
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic sub-key_shared-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Key_Shared \
    --num-messages 0
```

