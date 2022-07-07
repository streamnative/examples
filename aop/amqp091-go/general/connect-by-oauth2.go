package main

import (
	"fmt"
	"github.com/apache/pulsar-client-go/oauth2"
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
	oauth2Authentication, err := NewOAuth2Authentication(ClientCredentialsFlowOptions{
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
