# pylint: disable=missing-module-docstring
# pylint: disable=missing-class-docstring
# pylint: disable=missing-function-docstring
# pylint: disable=too-few-public-methods
import configparser
from confluent_kafka import Producer

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
    print(f'bootstrap_servers: {bootstrap_servers}, topic: {topic}')

    producer = Producer({
        'bootstrap.servers': bootstrap_servers,
        'sasl.mechanism': 'PLAIN',
        'security.protocol': 'SASL_SSL',
        'sasl.username': 'user',
        'sasl.password': f'token:{token}',
    })


    for i in range(10):
        producer.poll(0)
        producer.produce(topic, value=f'msg-{i}', callback=on_delivery)

    producer.poll()
