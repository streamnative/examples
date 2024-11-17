# Sarama examples
This folder contains example applications connecting to [StreamNative Cloud](https://console.streamnative.cloud) to demonstrate the use of Sarama. For code snippet examples on how to use the different types in Sarama, see [Sarama's API documentation on pkg.go.dev](https://pkg.go.dev/github.com/IBM/sarama)

## Cluster Setup

1. [Sign up for a StreamNative Cloud account](https://docs.streamnative.io/kafka-clients/kafka-go-introduction#sign-up-for-stream-native-cloud).

2. [Create a new cluster](https://docs.streamnative.io/kafka-clients/kafka-go-cluster-setup#create-a-stream-native-cloud-cluster).

3. [Get the bootstrap servers](https://docs.streamnative.io/kafka-clients/kafka-go-cluster-setup#get-the-kafka-service-url). Note down the bootstrap servers as you'll need them in the next steps.

4. [Create a service account and get the API key](https://docs.streamnative.io/kafka-clients/kafka-go-cluster-setup#create-a-service-account-and-api-key). For simplicity, you can create a super-user service account to run all the examples without worrying about authorization settings.


## Build the examples

1. Clone the repo.

   ```bash
   git clone https://github.com/streamnative/examples.git
   ```

2. Enter the `examples/cloud_kafka/sarama` directory.

   ```bash
   cd examples/cloud_kafka/sarama
   ```

3. Build the examples.

   ```bash
   sh build.sh
   ```

## Run the examples

In the terminal you are running the examples from, export the following environment variables with the values for your StreamNative Cloud cluster.

```bash
export BOOTSTRAP_SERVERS="<your-bootstrap-servers>"
export API_KEY="<your-api-key>"
```

### The following examples are adapted from the [Sarama Examples](https://github.com/IBM/sarama/tree/main/examples)

### Consumer Group

[consumer_group](./consumer_group) is a simple example that demonstrates how to use the Sarama consumer group client to consume messages from a Kafka topic.

```bash
./consumergroup/consumergroup -brokers=$BOOTSTRAP_SERVERS -apiKey=$API_KEY -topics="sarama-test" -group="sarama-example" 
```

### HTTP server

[http_server](./http_server) is a simple HTTP server uses both the sync producer to produce data as part of the request handling cycle, as well as the async producer to maintain an access log. It also uses the [mocks subpackage](https://pkg.go.dev/github.com/IBM/sarama/mocks) to test both.

```bash
./http_server/http_server -brokers=$BOOTSTRAP_SERVERS -apiKey=$API_KEY
```

### Interceptors

Basic example to use a producer interceptor that produces [OpenTelemetry](https://github.com/open-telemetry/opentelemetry-go/) spans and add some headers for each intercepted message.

```bash
./interceptors/interceptors -brokers=$BOOTSTRAP_SERVERS -apiKey=$API_KEY -topic="sarama-interceptor"
```

### Transactional Producer

[txn_producer](./txn_producer) Basic example to use a transactional producer that produce on some topic within a Kafka transaction. To ensure transactional-id uniqueness it implement some **_ProducerProvider_** that build a producer appending an integer that grow when producer is created.

```bash
./txn_producer/txn_producer -brokers=$BOOTSTRAP_SERVERS -apiKey=$API_KEY -topic="sarama-txn"
```

### Exacly-once transactional paradigm

[exactly_once](./exactly_once) Basic example to use a transactional producer that produce consumed message from some topics within a Kafka transaction. To ensure transactional-id uniqueness it implement some **_ProducerProvider_** that build a producer using current message topic-partition.

```bash
./exactly_once/exactly_once -brokers=$BOOTSTRAP_SERVERS -apiKey=$API_KEY -group="sarama-example" -destination-topic="sararma-exactly-once-destination" -topics="sarama-eo" 
```

### The following examples are additional examples

### Producer
[produce](./produce) is a simple example that demonstrates how to use the Sarama sync/async/batch producers to send messages to a Kafka topic

```bash
./produce/produce -brokers=$BOOTSTRAP_SERVERS -apiKey=$API_KEY -topics="sarama-produce"
```
