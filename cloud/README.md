# Overview

## Programming Languages

This directory includes examples of Apache Pulsar client applications, showcasing producers and consumers, written in various programming languages. The README for each language walks through the necessary steps to run each example. When each client establishes a connection with the Pulsar cluster through OAuth2, it needs to obtain the specified options from the Pulsar cluster and OAuth2 services. [How to get OAuth2 options](#How to get OAuth2 options) explains how you get these options.

Currently, we support the following three languages to connect through OAuth2:

- Java
- Go
- CPP

For clients in other languages, you can connect through token, reference to [here](https://pulsar.apache.org/docs/en/security-tls-transport/#client-configuration).

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
