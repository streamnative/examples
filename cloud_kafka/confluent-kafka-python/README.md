# confluent-kafka-python examples

This directory includes examples of Kafka client applications connect to [StreamNative Cloud](https://console.streamnative.cloud).

## Quick Start

Please ensure `poetry` is installed. If not, follow the instructions [here](https://python-poetry.org/docs/#installation).

Install the dependencies:

```bash
poetry install
```

## Configuration

Please fill the [`sncloud.ini`](./sncloud.ini) before running the examples. Then you can run `poetry run python xxx.py` to execute the `xxx.py` script.

The `common` section contains:
- `bootstrap.servers`: The URL to connect to the Kafka protocol endpoint. You can get it from StreamNative cloud console, it's usually in the format of `pc-xxx:9093`.
- `topic`: The topic name
- `token`: The token to authenticate with both the Kafka protocol endpoint and the Kafka Schema Registry. You can get it from StreamNative cloud console by creating a new API key.

The `consumer` section contains:
- `group.id`: The consumer group id

The `schema.registry` section contains:
- `url`: The URL to connect to the Kafka Schema Registry. You can get it from StreamNative cloud console, it's usually in the format of `https://pc-xxx/kafka`.

## Examples

### End to end example

- `producer.py`: It will send 10 messages to the topic.
- `consumer.py`: It will read messages from the topic and exit after receiving an interrupt signal (e.g., press `Ctrl+C`).

### Avro schema end to end example

It will use the following schema:

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

and the `User` Python class definition:

```python
class User:

    def __init__(self, name: str = None, age: int = 0):
        self.name = name
        self.age = age
```

- `avro_producer.py`: It will send a message (`User: {"name": "Alice", "age": 18}`) with Avro schema to the topic.
- `avro_consumer.py`: It will read messages from the topic and exit after receiving an interrupt signal (e.g., press `Ctrl+C`). All messages will be parsed to the `User` class, where invalid messages will be skipped.
