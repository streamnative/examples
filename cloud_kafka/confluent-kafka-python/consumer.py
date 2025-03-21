from confluent_kafka import Consumer, KafkaError, KafkaException
import configparser

config = configparser.ConfigParser()
config.read('sncloud.ini')

bootstrap_servers = config['common']['bootstrap.servers']
topic = config['common']['topic']
token = config['common']['token']
group = config['consumer']['group.id']
print(f'bootstrap_servers: {bootstrap_servers}, topic: {topic}, group: {group}')

def error_cb(err):
    print("Client error: {}".format(err))
    if err.code() == KafkaError._ALL_BROKERS_DOWN or \
            err.code() == KafkaError._AUTHENTICATION:
        raise KafkaException(err)

consumer = Consumer({
    'bootstrap.servers': bootstrap_servers,
    'group.id': group,
    'auto.offset.reset': 'earliest',
    'sasl.mechanism': 'PLAIN',
    'security.protocol': 'SASL_SSL',
    'sasl.username': 'user',
    'sasl.password': f'token:{token}',
    'error_cb': error_cb,
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
        print("Consumer error: {}".format(msg.error()))
        continue

    print('Received message: {}'.format(msg.value().decode('utf-8')))

consumer.close()
