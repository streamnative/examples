package main

import (
	"fmt"
	"log"

	"github.com/apache/pulsar-client-go/oauth2"
	amqp "github.com/rabbitmq/amqp091-go"
	amqpauth "github.com/streamnative/aop-amqp091-auth-go"
)

func main() {
	endpoint := "amqp://your-host-cluster:5671"

	oauth2Authentication, err := amqpauth.NewOAuth2Authentication(
		amqpauth.ClientCredentialsFlowOptions{
			ClientCredentialsFlowOptions: oauth2.ClientCredentialsFlowOptions{
				// your key file from here
				// https://docs.streamnative.io/cloud/stable/managed-access/service-account#work-with-a-service-account-through-streamnative-cloud-manager
				KeyFile: "/your-key-file-path.json",
			},
			// your audience from streamnative cloud console ui
			Audience: "your-audience",
		})
	saslConfigs := []amqp.Authentication{oauth2Authentication}
	conn, err := amqp.DialConfig(endpoint, amqp.Config{
		SASL:  saslConfigs,
		Vhost: "vhost2",
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
		"fanout",
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
