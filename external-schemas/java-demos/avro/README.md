# Usage

Requires JDK 17+ and Maven 3.8+.

```sh
export PULSAR_SERVICE_URL='pulsar+ssl://your-cluster:6651'
export SCHEMA_REGISTRY_URL='https://your-cluster/kafka'
export PULSAR_TOKEN='<raw-jwt-without-token-prefix>'

# Optional
export PULSAR_TOPIC='test-pulsar-external-avro-reference'
export PULSAR_SUBSCRIPTION='pulsar-avro-reference-sub'
export MESSAGE_COUNT=10

mvn clean package

# Send and receive MESSAGE_COUNT messages (default: 10)
java -cp 'target/classes:target/dependency/*' io.streamnative.examples.avro.SchemaProduce
java -cp 'target/classes:target/dependency/*' io.streamnative.examples.avro.SchemaConsume
```

In an IDE, import `pom.xml`, set the same environment variables, and run the two main classes.
