# Kafka Go Examples

This directory includes examples of Kafka client applications connect to [StreamNative Cloud](https://console.streamnative.cloud), showcasing producers, consumers, transactions, and other admin API usage, written using [Confluent Golang Client for Apache Kafka](https://github.com/confluentinc/confluent-kafka-go). 

## Cluster Setup

## Build the examples

1. Clone the repo.

   ```bash
   git clone https://github.com/streamnative/examples.git
   ```

2. Enter the `examples/cloud/kafka_go` directory.

   ```bash
   cd examples/cloud/kafka_go
   ```

3. Build the examples.

   ```bash
   sh build.sh
   ```

## Run the examples

In the terminal you are running the examples from, export the following environment variables with the values for your StreamNative Cloud cluster.

```bash
export BOOTSTRAP_SERVERS="<your-bootstrap-servers>"
export SCHEMA_REGISTRY_URL="<your-schema-registry-url>"
export API_KEY="<your-api-key>"
```

### Basic Operations

1. Create a topic.

    ```bash
    ./admin_create_topic/admin_create_topic $BOOTSTRAP_SERVERS $APIKEY basic_users 4 3
    ```

    The example will create a topic named `basic_users` with 4 partitions and 3 replicas. See source code [admin_create_topic/admin_create_topic.go](admin_create_topic/admin_create_topic.go) for more details.

2. Describe the topic.

    ```bash
    ./admin_describe_topics/admin_describe_topics $BOOTSTRAP_SERVERS $APIKEY false basic_users
    ```

    You should see the topic `basic_users` with 4 partitions. See source code [admin_describe_topics/admin_describe_topics.go](admin_describe_topics/admin_describe_topics.go) for more details.

3. Produce messages to the topic.

    ```bash
    ./producer_example/producer_example $BOOTSTRAP_SERVERS $APIKEY basic_users
    ```

    The examples will produce 3 messages to the topic. You will see the similar output to the following:

    ```
    Created Producer rdkafka#producer-1
    Delivered message to topic basic_users [3] at offset 0
    Delivered message to topic basic_users [0] at offset 0
    Delivered message to topic basic_users [2] at offset 0
    ```

    See source code [producer_example/producer_example.go](producer_example/producer_example.go) for more details.

4. Consume messages from the topic.

    ```bash
    ./consumer_example/consumer_example $BOOTSTRAP_SERVERS $APIKEY sub basic_users
    ```

    This example will consume messages from the topic with a subscription name `sub`. You should see the similar output to the following:

    ```bash
    Created Consumer rdkafka#consumer-1
    % Message on basic_users[0]@0:
    Producer example, message #1
    % Headers: [myTestHeader="header values are binary"]
    % Message on basic_users[3]@0:
    Producer example, message #0
    % Headers: [myTestHeader="header values are binary"]
    % Message on basic_users[2]@0:
    Producer example, message #2
    % Headers: [myTestHeader="header values are binary"]
    Ignored OffsetsCommitted (<nil>, [basic_users[0]@1 basic_users[1]@unset basic_users[2]@1 basic_users[3]@1])
    ```

    Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

    See source code [consumer_example/consumer_example.go](consumer_example/consumer_example.go) for more details.

5. Get the EARLIEST and LATEST offsets for the topic.

    You can get earliest offset of a topic partition by running the following command:

    ```bash
    ./admin_list_offsets/admin_list_offsets $BOOTSTRAP_SERVERS $APIKEY basic_users 1 EARLIEST
    ```

    You should see the output like the following:
    ```bash
    Topic: basic_users Partition: 1
	        Offset: 0 Timestamp: 0
    ```

    You can get latest offset of a topic partition by running the following command:

    ```bash
    ./admin_list_offsets/admin_list_offsets $BOOTSTRAP_SERVERS $APIKEY basic_users 1 LATEST
    ```

    You should see the output like the following:
    ```bash
    Topic: basic_users Partition: 1
	        Offset: 0 Timestamp: 0
    ```

    See source code [admin_list_offsets/admin_list_offsets.go](admin_list_offsets/admin_list_offsets.go) for more details.

6. List the consumer groups.

    ```bash
     ./admin_list_consumer_groups/admin_list_consumer_groups -b $BOOTSTRAP_SERVERS -api-key $APIKEY
    ```
    
    You should see the output like the following:

    ```bash
    A total of 1 consumer group(s) listed:
    GroupId: sub
    State: Empty
    Type: Unknown
    IsSimpleConsumerGroup: false
    ```

    See source code [admin_list_consumer_groups/admin_list_consumer_groups.go](admin_list_consumer_groups/admin_list_consumer_groups.go) for more details.

7. List the consumer group offsets.

    ```bash
    ./admin_list_consumer_group_offsets/admin_list_consumer_group_offsets $BOOTSTRAP_SERVERS $APIKEY sub false basic_users 1
    ```

    See source code [admin_list_consumer_group_offsets/admin_list_consumer_group_offsets.go](admin_list_consumer_group_offsets/admin_list_consumer_group_offsets.go) for more details.

8. Describe the consumer group.

    ```bash
    ./admin_describe_consumer_groups/admin_describe_consumer_groups $BOOTSTRAP_SERVERS $APIKEY false sub
    ```

    This example will describe the consumer group `sub`. See source code [admin_describe_consumer_groups/admin_describe_consumer_groups.go](admin_describe_consumer_groups/admin_describe_consumer_groups.go) for more details.

9. Delete the consumer group.

    ```bash
    ./admin_delete_consumer_groups/admin_delete_consumer_groups $BOOTSTRAP_SERVERS $APIKEY 60 sub
    ```

    This example will delete the consumer group `sub` with 60 seconds timeout. See source code [admin_delete_consumer_groups/admin_delete_consumer_groups.go](admin_delete_consumer_groups/admin_delete_consumer_groups.go) for more details.

    After the consumer group is deleted, the consumer group `sub` will no longer be listed. You can verify this by running the `admin_list_consumer_groups` example again.

    ```bash
    ./admin_list_consumer_groups/admin_list_consumer_groups -b $BOOTSTRAP_SERVERS -api-key $APIKEY
    ```

10. Delete the records from the topic.

    ```bash
    ./admin_delete_records/admin_delete_records $BOOTSTRAP_SERVERS $APIKEY basic_users 0 1
    ```

    The example will delete the records from the topic `basic_users` partition 0 offset 1. 

    You should see the output like the following:

    ```bash
    Delete records result for topic basic_users partition: 0
	Delete records succeeded
		New low-watermark: 0
    ```
    
    See source code [admin_delete_records/admin_delete_records.go](admin_delete_records/admin_delete_records.go) for more details.

11. Delete the topic.

    ```bash
    ./admin_delete_topics/admin_delete_topics $BOOTSTRAP_SERVERS $APIKEY basic_users
    ```

    This example will delete the topic `basic_users`. See source code [admin_delete_topics/admin_delete_topics.go](admin_delete_topics/admin_delete_topics.go) for more details.

    After the topic is deleted, the topic `basic_users` will no longer be listed. You can verify this by running the `admin_describe_topics` example again.

    ```bash
    ./admin_describe_topics/admin_describe_topics $BOOTSTRAP_SERVERS $APIKEY false basic_users
    ```

    You will see the following output:

    ```bash
    A total of 1 topic(s) described:

    Topic: basic_users has error: Broker: Unknown topic or partition
    ```

### Produce and consume generic AVRO messages

1. Produce generic AVRO messages.

    ```bash
    ./avro_generic_producer_example/avro_generic_producer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY generic_avro_users
    ```

    This example produce 1 message to the topic `generic_avro_users`. See source code [avro_generic_producer_example/avro_generic_producer_example.go](avro_generic_producer_example/avro_generic_producer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Producer rdkafka#producer-1
    Delivered message to topic generic_avro_users [0] at offset 0
    ```

2. Consume generic AVRO messages.

    ```bash
    ./avro_generic_consumer_example/avro_generic_consumer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY sub generic_avro_users
    ```

    This example will consume messages from the topic `generic_avro_users` with a subscription name `sub`. See source code [avro_generic_consumer_example/avro_generic_consumer_example.go](avro_generic_consumer_example/avro_generic_consumer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Consumer rdkafka#consumer-1
    % Message on generic_avro_users[0]@0:
    {Name:First user FavoriteNumber:42 FavoriteColor:blue}
    % Headers: [myTestHeader="header values are binary"]
    ```

    Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

### Produce and consume specific AVRO messages

1. Produce specific AVRO messages.

    ```bash
    ./avro_specific_producer_example/avro_specific_producer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY specific_avro_users
    ```

    This example produce 1 message to the topic `specific_avro_users`. See source code [avro_specific_producer_example/avro_specific_producer_example.go](avro_specific_producer_example/avro_specific_producer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Producer rdkafka#producer-1
    Delivered message to topic specific_avro_users [0] at offset 0
    ```

2. Consume specific AVRO messages.

    ```bash
    ./avro_specific_consumer_example/avro_specific_consumer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY sub specific_avro_users
    ```

    This example will consume messages from the topic `specific_avro_users` with a subscription name `sub`. See source code [avro_specific_consumer_example/avro_specific_consumer_example.go](avro_specific_consumer_example/avro_specific_consumer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Consumer rdkafka#consumer-1
    % Message on specific_avro_users[0]@0:
    {Name:First user Favorite_number:42 Favorite_color:blue}
    % Headers: [myTestHeader="header values are binary"]
    Ignored OffsetsCommitted (<nil>, [specific_avro_users[0]@1])
    ```

    Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

### Produce and consume Avro messages using Avro v2

1. Produce Avro v2 messages.

    ```bash
    ./avrov2_producer_example/avrov2_producer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY avrov2_users
    ```

    This example produce 1 message to the topic `avrov2_users`. See source code [avrov2_producer_example/avrov2_producer_example.go](avrov2_producer_example/avrov2_producer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Producer rdkafka#producer-1
    Delivered message to topic avrov2_users [0] at offset 0
    ```

2. Consume Avro v2 messages.

    ```bash
    ./avrov2_consumer_example/avrov2_consumer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY sub avrov2_users
    ```

    This example will consume messages from the topic `avrov2_users` with a subscription name `sub`. See source code [avrov2_consumer_example/avrov2_consumer_example.go](avrov2_consumer_example/avrov2_consumer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Consumer rdkafka#consumer-1
    % Message on avrov2_users[0]@0:
    {Name:First user FavoriteNumber:42 FavoriteColor:blue}
    % Headers: [myTestHeader="header values are binary"]
    Ignored OffsetsCommitted (<nil>, [avrov2_users[0]@1])
    ```

    Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

### Produce and consume JSON messages

1. Produce JSON messages.

    ```bash
    ./json_producer_example/json_producer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY json_users
    ```

    This example produce 1 message to the topic `json_users`. See source code [json_producer_example/json_producer_example.go](json_producer_example/json_producer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Producer rdkafka#producer-1
    Delivered message to topic json_users [0] at offset 0
    ```

2. Consume JSON messages.

    ```bash
    ./json_consumer_example/json_consumer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY sub json_users
    ```

    This example will consume messages from the topic `json_users` with a subscription name `sub`. See source code [json_consumer_example/json_consumer_example.go](json_consumer_example/json_consumer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Consumer rdkafka#consumer-1
    % Message on json_users[0]@0:
    {Name:First user FavoriteNumber:42 FavoriteColor:blue}
    % Headers: [myTestHeader="header values are binary"]
    Ignored OffsetsCommitted (<nil>, [json_users[0]@1])
    ```

    Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

### Produce and consume Protobuf messages

1. Produce Protobuf messages.

    ```bash
    ./protobuf_producer_example/protobuf_producer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY protobuf_users
    ```

    This example produce 1 message to the topic `protobuf_users`. See source code [protobuf_producer_example/protobuf_producer_example.go](protobuf_producer_example/protobuf_producer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Producer rdkafka#producer-1
    Delivered message to topic protobuf_users [0] at offset 0
    ```

2. Consume Protobuf messages.

    ```bash
    ./protobuf_consumer_example/protobuf_consumer_example $BOOTSTRAP_SERVERS $SCHEMA_REGISTRY $APIKEY sub protobuf_users
    ```

    This example will consume messages from the topic `protobuf_users` with a subscription name `sub`. See source code [protobuf_consumer_example/protobuf_consumer_example.go](protobuf_consumer_example/protobuf_consumer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Consumer rdkafka#consumer-1
    % Message on protobuf_users[0]@0:
    name:"First user" favorite_number:42 favorite_color:"blue"
    % Headers: [myTestHeader="header values are binary"]
    Ignored OffsetsCommitted (<nil>, [protobuf_users[0]@1])
    ```

    Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

### Consume offset with metadata

1. Produce messages to the topic.

    ```bash
    ./producer_example/producer_example $BOOTSTRAP_SERVERS $APIKEY basic_users_metadata
    ```

    This example will produce 3 messages to the topic `basic_users_metadata`. See source code [producer_example/producer_example.go](producer_example/producer_example.go) for more details.

    You should see the following output:

    ```bash
    Created Producer rdkafka#producer-1
    Delivered message to topic basic_users_metadata [0] at offset 0
    Delivered message to topic basic_users_metadata [0] at offset 1
    Delivered message to topic basic_users_metadata [0] at offset 2
    ```

2. Commit offset with metadata.

    ```bash
    ./consumer_offset_metadata/consumer_offset_metadata $BOOTSTRAP_SERVERS $APIKEY sub basic_users_metadata 0 2 "my_metadata"
    ```

    This example will commit the offset 2 with metadata `my_metadata` for the topic `basic_users_metadata` partition 0. See source code [consumer_offset_metadata/consumer_offset_metadata.go](consumer_offset_metadata/consumer_offset_metadata.go) for more details.

    You should see the following output:

    ```bash
    Created Consumer rdkafka#consumer-1
    Committing offset 2 with metadata my_metadata
    Partition 0 offset committed successfully
    ```

    You can verify the metadata by running the following command:

    ```bash
    ./consumer_offset_metadata/consumer_offset_metadata $BOOTSTRAP_SERVERS $APIKEY sub basic_users_metadata 0
    ```

    You should see the following output:

    ```bash
    Created Consumer rdkafka#consumer-1
    Committed partition 0 offset: 2 metadata: my_metadata
    ``` 

### Transactions Example

Please refer to [transactions_example](transactions_example/README.md) for more details.

