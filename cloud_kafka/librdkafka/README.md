# librdkafka examples

## Build the examples

You can just build and run examples with the following command:

```bash
./configure
cmake -B build
cmake --build build
```

A source file `xxx.cc` will be built into an executable `xxx` in the `build` directory.

## Configuration

Please fill the [`sncloud.ini`](./sncloud.ini) before running the examples. Then you can run `./build/avro_producer` or `./build/avro_consumer` to start the producer or consumer.

The `common` section contains:
- `bootstrap.servers`: The URL to connect to the Kafka protocol endpoint. You can get it from StreamNative cloud console, it's usually in the format of `pc-xxx:9093`.
- `topic`: The topic name
- `token`: The token to authenticate with both the Kafka protocol endpoint and the Kafka Schema Registry. You can get it from StreamNative cloud console by creating a new API key.

The `consumer` section contains:
- `group.id`: The consumer group id

The `schema.registry` section contains:
- `url`: The URL to connect to the Kafka Schema Registry. You can get it from StreamNative cloud console, it's usually in the format of `https://pc-xxx/kafka`.

## Examples

### Avro schema end to end example

It will use the schema defined in [utils.h](./utils.h):

```json
{
    "name": "User",
    "type": "record",
    "fields": [
        {
            "name": "name",
            "type": "string"
        },
        {
            "name": "age",
            "type": "int"
        }
    ]
}
```

- `avro_producer.cc`: It will send some messages serialized by `User` objects that follow the schema above to the topic.
- `avro_consumer.cc`: It will receive messages and parse the payload to `User` objects until there are no more messages.
