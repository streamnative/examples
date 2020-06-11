## Reader

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringReaderExample \
    --topic reader-example \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic reader-example \
    --num-messages 10
```

```bash
bin/pulsar-admin topics stats reader-example
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringReaderExample \
    --topic reader-example \
    --num-messages 0
```
