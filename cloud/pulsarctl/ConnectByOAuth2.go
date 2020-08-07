package main

import (
	"flag"
	"fmt"
	"github.com/apache/pulsar-client-go/oauth2"
	"github.com/streamnative/pulsarctl/pkg/auth"
	"github.com/streamnative/pulsarctl/pkg/pulsar"
	"github.com/streamnative/pulsarctl/pkg/pulsar/common"
	"log"
)

var (
	help          bool
	issuerUrl     string
	audience      string
	privateKey    string
	clientId      string
	webServiceURL string
)

func main() {
	flag.Parse()

	if help {
		flag.Usage()
	}

	pulsarCtlConfig := &common.Config{
		WebServiceURL:              webServiceURL,
		TLSAllowInsecureConnection: true,
	}
	issuer := oauth2.Issuer{
		IssuerEndpoint: issuerUrl,
		ClientID:       clientId,
		Audience:       audience,
	}
	oauth, err := auth.NewAuthenticationOAuth2WithDefaultFlow(issuer, privateKey)
	if err != nil {
		log.Fatal(err)
	}
	admin := pulsar.NewWithAuthProvider(pulsarCtlConfig, oauth)
	ns, err := admin.Namespaces().GetNamespaces("public")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("the namespace is: %s\n", ns)
}

func init() {
	flag.StringVar(&issuerUrl, "issuerUrl", "", "issuerUrl is a named external system that provides identity and API access by issuing OAuth access tokens")
	flag.StringVar(&audience, "audience", "", "audience is the address of the accessed service")
	flag.StringVar(&privateKey, "privateKey", "", "privateKey is the path of a service account to access your StreamNative Cloud Pulsar cluster")
	flag.StringVar(&clientId, "clientId", "", "clientId is a public identifier for apps")
	flag.StringVar(&webServiceURL, "serviceURL", "", "webServiceURL is the address of the accessed HTTP service")
	flag.BoolVar(&help, "help", false, "help cmd")
}
