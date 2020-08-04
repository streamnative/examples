package main

import (
	"fmt"
	"github.com/apache/pulsar-client-go/oauth2"
	"github.com/apache/pulsar-client-go/oauth2/store"
	"github.com/streamnative/pulsarctl/pkg/auth"
	"github.com/streamnative/pulsarctl/pkg/pulsar"
	"github.com/streamnative/pulsarctl/pkg/pulsar/common"
	"log"
)

func main() {
	keyFile := "private key file path"

	pulsarCtlConfig := &common.Config{
		WebServiceURL: "https://pulsar.service:8443",
	}

	issuer := oauth2.Issuer{
		IssuerEndpoint: "",
		ClientID:       "",
		Audience:       "",
	}

	memoryStore := store.NewMemoryStore()
	err := saveGrant(memoryStore, keyFile, issuer.Audience)
	if err != nil {
		log.Fatal(err)
	}

	oauth, err := auth.NewAuthenticationOAuth2(issuer, memoryStore)
	if err != nil {
		log.Fatal(err)
	}

	admin := pulsar.NewWithAuthProvider(pulsarCtlConfig, oauth)

	ns, err := admin.Namespaces().GetNamespaces("public")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println(ns)
}

func saveGrant(store store.Store, keyFile, audience string) error {
	flow, err := oauth2.NewDefaultClientCredentialsFlow(oauth2.ClientCredentialsFlowOptions{
		KeyFile:          keyFile,
		AdditionalScopes: nil,
	})
	if err != nil {
		return err
	}

	grant, err := flow.Authorize(audience)
	if err != nil {
		return err
	}

	return store.SaveGrant(audience, *grant)
}
