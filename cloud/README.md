# Overview

## Programming Languages

This directory includes examples of Apache Pulsar client applications, showcasing producers and consumers, written in various programming languages. The README for each language walks through the necessary steps to run each example. When each client establishes a connection with the Pulsar cluster through **OAuth2** or **Token**.
 
For the OAuth2, it needs to obtain the specified options from the Pulsar cluster and OAuth2 services. [How to get OAuth2 options](#How to get OAuth2 options) explains how you get these options.

Currently, we support the following three languages to connect through OAuth2:

- Java
- Go
- CPP

The following clients and Pulsar CLI tools are supported to connect to cluster through the Token. And [How to get Token options](#How to get Token options) explains how you get these options.

- Java
- Go
- CPP
- Python
- CSharp(TODO)
- NodeJS
- pulsarctl
- pulsar-admin
- pulsar-client
- pulsar-perf

## How to get Token options

When you use Token to connect to Pulsar cluster, you need to provide the following options:

- `SERVICE_URL`
- `WEB_SERVICE_URL`
- `AUTH_PARAMS`

For the `SERVICE_URL` field, you can get the **hostname** through the following command:

```shell script
$ snctl get pulsarclusters [CLUSTER_NAME] -n [NAMESPACE] -o json | jq '.spec.serviceEndpoints[0].dnsName'
```

Output:

```text
api.test.cloud.xxx.streamnative.dev
```

A `SERVICE_URL` is a combination of protocol, hostname and port, so an example of a complete `SERVICE_URL` is as follows:


```text
pulsar://api.test.cloud.xxx.streamnative.dev:6650

# For tls
pulsar+ssl://api.test.cloud.xxx.streamnative.dev:6651
```

For the `WEB_SERVICE_URL` field, you can get the **hostname** through the following command:

```shell script
$ snctl get pulsarclusters [CLUSTER_NAME] -n [NAMESPACE] -o json | jq '.spec.serviceEndpoints[0].dnsName'
```

Output:

```text
api.test.cloud.xxx.streamnative.dev
```

A `WEB_SERVICE_URL` is a combination of protocol, hostname and port, so an example of a complete `WEB_SERVICE_URL` is as follows:

```text
http://api.test.cloud.xxx.streamnative.dev:8080

# For tls
https://api.test.cloud.xxx.streamnative.dev:8443
```

For the `AUTH_PARAMS` field, you can get it through the following command:

```shell script
$ snctl auth get-token [INSTANCE] [flags]
```

> Tips: In code implementation, for safety and convenience, you can consider setting `AUTH_PARAMS` as an environment variable.

## How to get OAuth2 options

When configuring and using OAuth2, you need to specify the following parameters:

- `type`
- `issuerUrl`
- `audience`
- `privateKey`
- `clientId`

For the OAuth2 `type` field, currently you only support `client_credentials`. So the value of the current type field is `client_credentials`

For the `privateKey` field, you need to get the path of a private key data file. The following example shows how to get this file:

```shell script
$ snctl auth export-service-account [NAME] [flags]

Flags:
  -h, --help              help for export-service-account
  -f, --key-file string   Path to the private key file.
      --no-wait           Skip waiting for service account readiness.
```

Output:

```text
Wrote private key file <Path of your private key file>
```

For the `clientId` and `issuerUrl` fields, you can get the corresponding value from the `privateKey` file. The following example shows how to get the `clientId` and `issueUrl` fields.

```text
{
"type":"sn_service_account",
"client_id":"0123456789abcdefg",
"client_secret":"ABCDEFG-JzAFKtj0Dcub9KF1WKN-qhFHBvgfAU_123456789-KI9",
"client_email":"test@gtest.auth0.com",
"issuer_url":"https://test.auth0.com"
}
```

For the `audience` field, is the address of the accessed service.
