package main

import (
	"fmt"
	"github.com/apache/pulsar-client-go/oauth2"
	"log"

	amqp "github.com/rabbitmq/amqp091-go"
)

type ClientCredentialsFlowOptions struct {
	oauth2.ClientCredentialsFlowOptions
	Audience string
}

type tokenAuthentication struct {
	token string
}

func (o *tokenAuthentication) Mechanism() string {
	return "token"
}

func (o *tokenAuthentication) Response() string {
	return fmt.Sprintf("%s", o.token)
}

var _ amqp.Authentication = &tokenAuthentication{}

func NewTokenAuthentication(token string) (amqp.Authentication, error) {
	return &tokenAuthentication{token: token}, nil
}

func NewOAuth2Authentication(options ClientCredentialsFlowOptions) (amqp.Authentication, error) {
	flow, err := oauth2.NewDefaultClientCredentialsFlow(options.ClientCredentialsFlowOptions)
	if err != nil {
		return nil, err
	}

	grant, err := flow.Authorize(options.Audience)
	if err != nil {
		return nil, err
	}

	return &tokenAuthentication{token: grant.Token.AccessToken}, nil
}

func main() {
	endpoint := "amqps://your-host-cluster:5671"

	oauth2Authentication, err := NewOAuth2Authentication(ClientCredentialsFlowOptions{
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