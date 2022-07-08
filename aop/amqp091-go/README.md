# Overview

The `amqp091-go` is a AMQP client written by RabbitMQ.

# Prerequisites

- Go 1.18 or higher version

# Running steps

**Please first create the namespace, which will be configured to vhost**

1. Clone example project

```
git clone https://github.com/streamnative/examples.git 
```

2. Download dependencies

```
cd examples/aop/amqp091-go
go mod tidy
```

3. Configure endpoint, audience, keyfile and vhost on producer/oauth-producer.go and consumer/oauth-consumer.go


4. Build and run example

```
go build producer/oauth-producer.go
go build consumer/oauth-consumer.go
./oauth-consumer
./oauth-producer
```
