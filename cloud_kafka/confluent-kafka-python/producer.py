from confluent_kafka import Producer
import configparser

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

def acked(err, msg):
    if err is not None:
        print('Failed to deliver message: {}'.format(err.str()))
    else:
        print('Produced to: {} [{}] @ {}'.format(msg.topic(), msg.partition(), msg.offset()))

for i in range(10):
    producer.poll(0)
    producer.produce(topic, value=f'msg-{i}', callback=acked)

producer.poll()
