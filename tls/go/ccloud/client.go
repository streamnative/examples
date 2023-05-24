// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package ccloud

import (
	"flag"
	"log"

	"github.com/apache/pulsar-client-go/pulsar"
)

var (
	help       bool
	issuerUrl  string
	audience   string
	privateKey string
	clientId   string
	serviceURL string
)

func CreateClient() pulsar.Client {
	flag.Parse()

	if help {
		flag.Usage()
	}

	oauth := pulsar.NewAuthenticationOAuth2(map[string]string{
		"type":       "client_credentials",
		"issuerUrl":  issuerUrl,
		"audience":   audience,
		"privateKey": privateKey,
		"clientId":   clientId,
	})

	client, err := pulsar.NewClient(pulsar.ClientOptions{
		URL:            serviceURL,
		Authentication: oauth,
	})
	if err != nil {
		log.Fatal(err)
	}
	return client
}

func init() {
	flag.StringVar(&issuerUrl, "issuerUrl", "", "issuerUrl is a named external system that provides identity and API access by issuing OAuth access tokens")
	flag.StringVar(&audience, "audience", "", "audience is the address of the accessed service")
	flag.StringVar(&privateKey, "privateKey", "", "privateKey is the path of a service account to access your StreamNative Cloud Pulsar cluster")
	flag.StringVar(&clientId, "clientId", "", "clientId is a public identifier for apps")
	flag.StringVar(&serviceURL, "serviceURL", "", "serviceURL is the address of the accessed broker")
	flag.BoolVar(&help, "help", false, "help cmd")
}
