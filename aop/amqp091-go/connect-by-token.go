package main

import (
	amqp "github.com/rabbitmq/amqp091-go"
)

func main() {
	token := "your-token"
	tokenAuthentication, err := NewTokenAuthentication(token)
	if err != nil {
		panic(err)
	}

	endpoint := "amqp://localhost:15002"
	saslConfigs := []amqp.Authentication{tokenAuthentication}
	connection, err := amqp.DialConfig(endpoint,
		amqp.Config{
			SASL:  saslConfigs,
			Vhost: "vhost1",
		})
	if err != nil {
		panic(err)
	}
	defer connection.Close()
}
