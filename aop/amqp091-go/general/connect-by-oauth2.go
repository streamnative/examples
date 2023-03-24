package main

import (
	"log"

	"github.com/apache/pulsar-client-go/oauth2"
	amqp "github.com/rabbitmq/amqp091-go"
	amqpauth "github.com/streamnative/aop-amqp091-auth-go"
)

func main() {
	endpoint := "amqp://localhost:15002"

	oauth2Authentication, err := amqpauth.NewOAuth2Authentication(amqpauth.ClientCredentialsFlowOptions{
		ClientCredentialsFlowOptions: oauth2.ClientCredentialsFlowOptions{
			KeyFile: "./oauth2/client-credentials.json",
		},
		Audience: "your-audience",
	})
	if err != nil {
		log.Fatalf("NewOAuth2Authentication: %v", err)
	}

	saslConfigs := []amqp.Authentication{oauth2Authentication}
	connection, err := amqp.DialConfig(endpoint,
		amqp.Config{
			SASL:  saslConfigs,
			Vhost: "vhost1",
		})
	if err != nil {
		log.Fatalf("failed to open connection: %v", err)
	}
	defer connection.Close()
}
