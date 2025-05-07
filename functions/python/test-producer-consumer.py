from pulsar import Client, SerDe

class MyObject(object):
  def __init__(self):
    self.a = 0
    self.b = 0

class CustomSerDe(SerDe):
  def __init__(self):
    pass
  def serialize(self, object):
    return ("%d,%d" % (object.a, object.b)).encode('utf-8')
  def deserialize(self, input_bytes):
    split = str(input_bytes.decode()).split(',')
    retval = MyObject()
    retval.a = int(split[0])
    retval.b = int(split[1])
    return retval

client = Client('pulsar://localhost:6650')
producer1 = client.create_producer(topic='input-topic-1')
producer2 = client.create_producer(topic='input-topic-2')
consumer = client.subscribe(
                  topic='output-topic-3',
                  subscription_name='my-subscription')

ser = CustomSerDe()
for i in range(100):
  dataObject = MyObject()
  dataObject.a = i
  dataObject.b = i + 100
  d = ser.serialize(dataObject)
  producer1.send(d)
  producer2.send(d)
  print('send msg "%d", "%d"' % (dataObject.a, dataObject.b))
for i in range(100):
    msg = consumer.receive()
    consumer.acknowledge(msg)
    ex = msg.value()
    d = ser.deserialize(ex)
    print('receive and ack msg "%d", "%d"' % (d.a, d.b))
