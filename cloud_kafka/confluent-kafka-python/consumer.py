# pylint: disable=missing-module-docstring
# pylint: disable=missing-class-docstring
# pylint: disable=missing-function-docstring
# pylint: disable=too-few-public-methods
import configparser
from confluent_kafka import Consumer

if __name__ == '__main__':
    config = configparser.ConfigParser()
    config.read('sncloud.ini')

    bootstrap_servers = config['common']['bootstrap.servers']
    topic = config['common']['topic']
    token = config['common']['token']
    group = config['consumer']['group.id']
    print(f'bootstrap_servers: {bootstrap_servers}, topic: {topic}, group: {group}')

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

        print(f'Received message: {msg.value().decode("utf-8")}')

    consumer.close()
