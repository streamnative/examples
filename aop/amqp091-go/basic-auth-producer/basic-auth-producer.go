package main

import (
	amqp "github.com/rabbitmq/amqp091-go"
	"log"
)

func main() {
	endpoint := "amqps://user:password@cluster-endpoint:5671/vhost4"

	conn, err := amqp.DialConfig(endpoint, amqp.Config{
		SASL: nil,
	})
	if err != nil {
		log.Fatalf("failed to open connection: %v", err)
	}

	channel, err := conn.Channel()
	if err != nil {
		log.Fatalf("failed to open channel: %v", err)
	}

	exchange := "exchange-3"
	if err = channel.ExchangeDeclare(
		exchange,
		"fanout",
		true,
		false,
		false,
		false,
		nil,
	); err != nil {
		log.Fatalf("ExchangeDeclare: %v", err)
	}

	if err := channel.Publish(
		exchange,
		"",
		false,
		false,
		amqp.Publishing{
			ContentType:  "text/plain",
			Body:         []byte("Hello"),
			DeliveryMode: amqp.Transient,
		}); err != nil {
		log.Fatalf("failed to produce a message: %v", err)
	}

	log.Printf("the \"%s\" message has been pulished", "Hello")
}
