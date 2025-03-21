# confluent-kafka-python examples

This directory includes examples of Kafka client applications connect to [StreamNative Cloud](https://console.streamnative.cloud).

## Quick Start

Please ensure `poetry` is installed. If not, follow the instructions [here](https://python-poetry.org/docs/#installation).

Install the dependencies:

```bash
poetry install
```

## Configuration

Please fill the [`sncloud.ini`](./sncloud.ini) before running the examples.

## Examples

### `producer.py`

```bash
poetry run python producer.py
```

It will send 10 messages to the topic.

### `consumer.py`

```bash
poetry run python consumer.py
```

It will read messages from the topic and exit after receiving an interrupt signal (e.g., press `Ctrl+C`).
