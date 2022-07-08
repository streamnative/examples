package main

import (
	amqp "github.com/rabbitmq/amqp091-go"
)

func main() {
	endpoint := "amqp://user:password@your-host-cluster:5671/vhost2"

	connection, err := amqp.Dial(endpoint)
	if err != nil {
		panic(err)
	}
	defer connection.Close()
}
