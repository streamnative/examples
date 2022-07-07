package main

import (
	"github.com/apache/pulsar-client-go/oauth2"
	amqp "github.com/rabbitmq/amqp091-go"
	aopamqpauth "github.com/streamnative/aop-amqp091-auth-go"
)

func main() {
	oauth2Authentication, err := aopamqpauth.NewOAuth2Authentication(aopamqpauth.ClientCredentialsFlowOptions{
		ClientCredentialsFlowOptions: oauth2.ClientCredentialsFlowOptions{
			KeyFile: "./oauth2/client-credentials.json",
		},
		Audience: "your-audience",
	})
	if err != nil {
		panic(err)
	}

	endpoint := "amqp://localhost:15002"
	saslConfigs := []amqp.Authentication{oauth2Authentication}
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
