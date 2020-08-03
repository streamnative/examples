# Overview

Produce message to and consume message from a Pulsar cluster using [Apache pulsar-client-go](https://github.com/apache/pulsar-client-go).

# Prerequisites

If you have not installed Go, install it according to the [installation instruction](http://golang.org/doc/install).

Since the `go mod` package management tool is used in this project, **Go 1.11 or higher** version is required.

- Go 1.11+
- pulsar-client-go 0.1.1+(not include 0.1.1)

# Example

In this example, the producer will publish data to the `topic-1` in your Pulsar cluster.
The content of each message payload is a combination of `hello-` and the 10 numbers 0-9 (e.g: `hello-0`).
The consumer will receive the message form the `topic-1` and `ack` the receipt of each message received.

1. Run the consumer, and start to receiving the message from `topic-1`:

```bash
$ go build -o consumer consumer.go
$ ./consumer
```

Output:

```text
time="2020-08-03T09:11:16+08:00" level=info msg="Created consumer" name=yulhd subscription=my-sub topic="persistent://public/default/topic-1"
Received message msgId: {27 0 0 0 <nil> 0xc0002ce000 {13817596638641025024 7933499488 0x4cc9f80}} -- content: 'hello-0'
Received message msgId: {27 1 0 0 <nil> 0xc0002ce000 {13817596638653740024 7946215066 0x4cc9f80}} -- content: 'hello-1'
Received message msgId: {27 2 0 0 <nil> 0xc0002ce000 {13817596638653748024 7946222530 0x4cc9f80}} -- content: 'hello-2'
Received message msgId: {27 3 0 0 <nil> 0xc0002ce000 {13817596638653750024 7946224792 0x4cc9f80}} -- content: 'hello-3'
Received message msgId: {27 4 0 0 <nil> 0xc0002ce000 {13817596638653752024 7946226367 0x4cc9f80}} -- content: 'hello-4'
Received message msgId: {27 5 0 0 <nil> 0xc0002ce000 {13817596638666750024 7959224273 0x4cc9f80}} -- content: 'hello-5'
Received message msgId: {27 6 0 0 <nil> 0xc0002ce000 {13817596638666760024 7959233979 0x4cc9f80}} -- content: 'hello-6'
Received message msgId: {27 7 0 0 <nil> 0xc0002ce000 {13817596638666764024 7959238686 0x4cc9f80}} -- content: 'hello-7'
Received message msgId: {27 8 0 0 <nil> 0xc0002ce000 {13817596638666766024 7959240510 0x4cc9f80}} -- content: 'hello-8'
Received message msgId: {27 9 0 0 <nil> 0xc0002ce000 {13817596638677280024 7969754185 0x4cc9f80}} -- content: 'hello-9'
time="2020-08-03T09:11:24+08:00" level=info msg="The consumer[1] successfully unsubscribed" name=yulhd subscription=my-sub topic="persistent://public/default/topic-1"
```

2. Run the producer and publish messages to the `topic-1`:

```bash
$ go build -o producer prodcer.go
$ ./producer
```

Output:

```text
2020/08/03 09:11:24 Published message:  {27 0 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 1 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 2 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 3 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 4 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 5 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 6 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 7 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 8 0 0 <nil> <nil> {0 0 <nil>}}
2020/08/03 09:11:24 Published message:  {27 9 0 0 <nil> <nil> {0 0 <nil>}}
time="2020-08-03T09:11:24+08:00" level=info msg="Closing producer" producer_name=standalone-1-1 topic="persistent://public/default/topic-1"
time="2020-08-03T09:11:24+08:00" level=info msg="Closed producer" producer_name=standalone-1-1 topic="persistent://public/default/topic-1"```
```
