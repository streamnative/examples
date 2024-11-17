# franz-go Kafka Go Example

This directory includes an example of a Kafka client application that connects to StreamNative Cloud using [franz-go](https://github.com/twmb/franz-go) client.

## Setup a Serverless Kafka cluster on StreamNative Cloud

> Note: If you have already created a cluster, you can skip the following steps and go to [Build the example](#build-the-example).

1. [Sign up for a StreamNative Cloud account](https://docs.streamnative.io/kafka-clients/kafka-go-introduction#sign-up-for-stream-native-cloud).

2. [Create a new cluster](https://docs.streamnative.io/kafka-clients/kafka-go-cluster-setup#create-a-stream-native-cloud-cluster).

3. [Get the bootstrap servers and schema registry URL](https://docs.streamnative.io/kafka-clients/kafka-go-cluster-setup#get-the-kafka-service-url). Note down the bootstrap servers and schema registry URL as you'll need them in the next steps.

4. [Create a service account and get the API key](https://docs.streamnative.io/kafka-clients/kafka-go-cluster-setup#create-a-service-account-and-api-key). For simplicity, you can create a super-user service account to run all the examples without worrying about authorization settings.

## Build the example

1. Clone the repo.

   ```bash
   git clone https://github.com/streamnative/examples.git
   ```

2. Enter the `examples/cloud_kafka/franz-go/basic` directory.

   ```bash
   cd examples/cloud_kafka/franz-go/basic
   ```

3. Build the example.

   ```bash
   go build -o streamnative-franz-example
   ```

## Run the example

In the terminal you are running the examples from, export the following environment variables with the values for your StreamNative Cloud cluster.

```bash
export BOOTSTRAP_SERVERS="<your-bootstrap-servers>"
export API_KEY="<your-api-key>"
```

After that, you can run the example by executing the following command. It will create a topic named `franz_test_topic` and produce 24 messages to it, then consume the messages from the topic.

```bash
./streamnative-franz-example -brokers $BOOTSTRAP_SERVERS -group sub -topic franz_test_topic -username unsed -password token:$APIKEY
```

Once you are done with the application, enter `Ctrl-C` to terminate the application.