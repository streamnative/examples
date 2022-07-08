package main

import (
	"log"

	amqp "github.com/rabbitmq/amqp091-go"
	amqpauth "github.com/streamnative/aop-amqp091-auth-go"
)

func main() {
	endpoint := "amqp://localhost:15002"

	token := "your-token"
	tokenAuthentication, err := amqpauth.NewTokenAuthentication(token)
	if err != nil {
		log.Fatalf("NewTokenAuthentication: %v", err)
	}

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
