package main

import (
	"fmt"
	"log"

	amqp "github.com/rabbitmq/amqp091-go"
)

func main() {
	endpoint := "amqp://localhost:15002"
	conn, err := amqp.DialConfig(endpoint, amqp.Config{
		SASL:  nil,
		Vhost: "vhost1",
	})
	if err != nil {
		log.Fatalf("failed to open connection: %v", err)
	}

	channel, err := conn.Channel()
	if err != nil {
		log.Fatalf("failed to open channel: %v", err)
	}

	exchange := "exchange-1"
	if err = channel.ExchangeDeclare(
		exchange,
		"direct",
		true,
		false,
		false,
		false,
		nil,
	); err != nil {
		log.Fatalf("ExchangeDeclare: %v", err)
	}

	queueName := "topic-1"
	queue, err := channel.QueueDeclare(
		queueName,
		true,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		log.Fatalf("QueueDeclare: %v", err)
	}

	if err = channel.QueueBind(
		queue.Name,
		"",
		exchange,
		false,
		nil,
	); err != nil {
		log.Fatalf("QueueBind: %v", err)
	}

	deliveries, err := channel.Consume(
		queue.Name,
		"",
		false,
		false,
		false,
		false,
		nil,
	)

	for delivery := range deliveries {
		fmt.Printf("received a message: %s", delivery.Body)
		_ = delivery.Ack(false)
	}
}
