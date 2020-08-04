package main

import (
	"fmt"
	"github.com/apache/pulsar-client-go/oauth2"
	"github.com/streamnative/pulsarctl/pkg/auth"
	"github.com/streamnative/pulsarctl/pkg/pulsar"
	"github.com/streamnative/pulsarctl/pkg/pulsar/common"
	"log"
)

func main() {
	keyFile := "/path/to/keyfile"

	pulsarCtlconfig := &common.Config{
		WebServiceURL:              "https://pulsar.service",
		TLSAllowInsecureConnection: true,
	}
	issuer := oauth2.Issuer{
		IssuerEndpoint: "https://oauth2.service",
		ClientID:       "0Xx..Yyxeny",
		Audience:       "audience",
	}
	oauth, err := auth.NewAuthenticationOAuth2WithDefaultFlow(issuer, keyFile)
	if err != nil {
		log.Fatal(err)
	}
	admin := pulsar.NewWithAuthProvider(pulsarCtlconfig, oauth)
	ns, err := admin.Namespaces().GetNamespaces("public")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("the namespace is: %s\n", ns)
}
