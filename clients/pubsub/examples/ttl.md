## TTL

```bash
bin/pulsar-admin namespaces create public/ttl-example
```

```bash
bin/pulsar-admin namespaces set-message-ttl -ttl 5 public/ttl-example
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringConsumerExample \
    --topic public/ttl-example/ttl-example \
    --subscription-name example-sub \
    --subscription-initial-position Earliest \
    --subscription-type Shared \
    --num-messages 0
```

```bash
java -cp pubsub/target/pulsar-pubsub-examples.jar \
    io.streamnative.examples.pubsub.SyncStringProducerExample \
    --topic public/ttl-example/ttl-example \
    --num-messages 100
```

Run producer multiple times.

```bash
bin/pulsar-admin topics stats public/ttl-example/ttl-example
```

Wait for TTL.

```bash
bin/pulsar-admin topics stats public/ttl-example/ttl-example
```
