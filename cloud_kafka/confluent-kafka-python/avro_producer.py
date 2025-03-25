# pylint: disable=missing-module-docstring
# pylint: disable=missing-class-docstring
# pylint: disable=missing-function-docstring
# pylint: disable=too-few-public-methods
import configparser
from confluent_kafka import Producer
from confluent_kafka.schema_registry import SchemaRegistryClient
from confluent_kafka.schema_registry.avro import AvroSerializer
from confluent_kafka.serialization import MessageField, SerializationContext

class User:

    def __init__(self, name: str, age: int):
        self.name = name
        self.age = age


SCHEMA_STR = """
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
"""

def on_delivery(err, msg):
    if err is not None:
        print(f'Failed to deliver message: {err.str()}')
    else:
        print(f'Produced to: {msg.topic()} [{msg.partition()}] @ {msg.offset()}')


if __name__ == '__main__':
    config = configparser.ConfigParser()
    config.read('sncloud.ini')

    bootstrap_servers = config['common']['bootstrap.servers']
    topic = config['common']['topic']
    token = config['common']['token']
    schema_registry_url = config['schema.registry']['url']
    print(f'bootstrap_servers: {bootstrap_servers}, topic: {topic}')

    schema_registry_client = SchemaRegistryClient({
            'url': schema_registry_url,
            'basic.auth.user.info': f'user:{token}'
        })
    avro_serializer = AvroSerializer(schema_registry_client,
                                     SCHEMA_STR,
                                     lambda user, _: {'name': user.name, 'age': user.age})

    producer = Producer({
        'bootstrap.servers': bootstrap_servers,
        'sasl.mechanism': 'PLAIN',
        'security.protocol': 'SASL_SSL',
        'sasl.username': 'user',
        'sasl.password': f'token:{token}',
    })

    user = User('Alice', 18)
    producer.produce(topic,
                     value=avro_serializer(user, SerializationContext(topic, MessageField.VALUE)),
                     callback=on_delivery)
    producer.poll()
