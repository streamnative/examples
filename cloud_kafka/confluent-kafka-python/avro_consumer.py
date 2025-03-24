# pylint: disable=missing-module-docstring
# pylint: disable=missing-class-docstring
# pylint: disable=missing-function-docstring
# pylint: disable=too-few-public-methods
import configparser
from confluent_kafka import Consumer
from confluent_kafka.schema_registry import SchemaRegistryClient
from confluent_kafka.schema_registry.avro import AvroDeserializer
from confluent_kafka.serialization import MessageField, SerializationContext, SerializationError

class User:

    def __init__(self, name: str = None, age: int = 0):
        self.name = name
        self.age = age


def dict_to_user(obj, _):
    if obj is None:
        return None
    return User(name=obj['name'], age=obj['age'])


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

if __name__ == '__main__':
    config = configparser.ConfigParser()
    config.read('sncloud.ini')

    bootstrap_servers = config['common']['bootstrap.servers']
    topic = config['common']['topic']
    token = config['common']['token']
    group = config['consumer']['group.id']
    schema_registry_url = config['schema.registry']['url']
    print(f'bootstrap_servers: {bootstrap_servers}, topic: {topic}, group: {group}')

    schema_registry_client = SchemaRegistryClient({
            'url': schema_registry_url,
            'basic.auth.user.info': f'user:{token}'
        })
    avro_deserializer = AvroDeserializer(schema_registry_client,
                                         SCHEMA_STR,
                                         dict_to_user)

    consumer = Consumer({
        'bootstrap.servers': bootstrap_servers,
        'group.id': group,
        'auto.offset.reset': 'earliest',
        'sasl.mechanism': 'PLAIN',
        'security.protocol': 'SASL_SSL',
        'sasl.username': 'user',
        'sasl.password': f'token:{token}',
        'error_cb': lambda err: print(f'Error: {err}'),
        'isolation.level': 'read_uncommitted',
    })

    consumer.subscribe([topic])

    while True:
        try:
            msg = consumer.poll(1.0)
        except KeyboardInterrupt:
            print('quit')
            break

        if msg is None:
            continue
        if msg.error():
            print(f'Consumer error: {msg.error()}')
            continue

        position = f'{msg.topic()}[{msg.partition()}] @ {msg.offset()}'
        try:
            user = avro_deserializer(msg.value(),
                                     SerializationContext(msg.topic(), MessageField.VALUE))
            if user is not None:
                print(f'Received User(name: {user.name}, age: {user.age}) from {position}')
        except SerializationError as e:
            print(f'Received invalid message from {position}: {e}')

    consumer.close()
