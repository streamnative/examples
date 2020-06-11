## Exclusive

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SeekConsumerExample \
    --topic sub-seek-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Exclusive \
    --num-messages 10 \
    --ack-every-n-messages 1 \
    --ack-type Individual
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic sub-seek-example \
    --num-messages 10
```

```bash
bin/pulsar-admin topics stats sub-seek-example
```