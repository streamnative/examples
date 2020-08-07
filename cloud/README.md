# Overview

## Programming Languages

This directory includes examples of Apache Pulsar client applications, showcasing producers and consumers, written in various programming languages. The README for each language walks through the necessary steps to run each example. When each client establishes a connection with the Pulsar cluster through **OAuth2** or **Token**.
 
For the OAuth2, it needs to obtain the specified options from the Pulsar cluster and OAuth2 services. [How to get OAuth2 options](#How to get OAuth2 options) explains how you get these options.

Currently, we support the following clients and `pulsarctl` to connect through OAuth2:

- Java
- Go
- CPP
- pulsarctl

The following clients and Pulsar CLI tools are supported to connect to cluster through the Token. And [How to get Token options](#How to get Token options) explains how you get these options.

- Java
- Go
- CPP
- Python
- NodeJS
- pulsarctl
- pulsar-admin
- pulsar-client
- pulsar-perf

Before starting, we assume that the `service account`, `pulsar instance` and `pulsar cluster` have been created in the current environment, and their `names` and `namespace` as follows:

```text
# service account
name: test-sa-name
namespace: test-sa-ns

# pulsar instance
name: test-pi-name
namespace: test-pi-ns

# pulsar cluster
name: test-pc-name
namespace: test-pc-ns
```

## How to get service URL

- `SERVICE_URL`
- `WEB_SERVICE_URL`

For the `SERVICE_URL` field, you can get the **hostname** through the following command:

```shell script
$ snctl pulsarclusters get test-pc-name -n test-pc-ns -o json | jq '.spec.serviceEndpoints[0].dnsName'
```

Output:

```text
cloud.streamnative.dev
```

A `SERVICE_URL` is a combination of protocol, hostname and port, so an example of a complete `SERVICE_URL` is as follows:


```text
pulsar://cloud.streamnative.dev:6650

# For tls
pulsar+ssl://cloud.streamnative.dev:6651
```

For the `WEB_SERVICE_URL` field, you can get the **hostname** through the following command:

```shell script
$ snctl get pulsarclusters test-pc-name -n test-pc-ns -o json | jq '.spec.serviceEndpoints[0].dnsName'
```

Output:

```text
cloud.streamnative.dev
```

A `WEB_SERVICE_URL` is a combination of protocol, hostname and port, so an example of a complete `WEB_SERVICE_URL` is as follows:

```text
http://cloud.streamnative.dev:8080

# For tls
https://cloud.streamnative.dev:443
```

## How to get token options

When you use Token to connect to Pulsar cluster, you need to provide the following options:

- `AUTH_PARAMS`

For the `AUTH_PARAMS` field, you can get it through the following command:

```shell script
$ snctl auth get-token [INSTANCE] [flags]

# e.g:
$ snctl auth get-token test-pi-name -n test-pi-ns --login
```

Output:

```text
We've launched your web browser to complete the login process.
Verification code: ABCD-EFGH

Waiting for login to complete...
Logged in as cloud@streamnative.io.
Welcome to Apache Pulsar!

Use the following access token to access Pulsar instance '[PULSAR_INSTANCE_NAMESPACE]/[PULSAR_INSTANCE_NAME]':

abcdefghijklmnopqrstuiwxyz0123456789
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

#e.g:
$ snctl auth export-service-account test-sa-name -n test-sa-ns -f [/path/to/key/file.txt]
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

For the `audience` field, it is the combination of the name and namespace of pulsar instance and `urn:sn:pulsar`, the example as follows:

```text
urn:sn:pulsar:test-pi-ns:test-pi-name
```

You can get all the pulsar instances in the current environment through the following command:

```shell script
$ snctl pulsarinstances get -A
```

