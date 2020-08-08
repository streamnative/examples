# Overview

The `pulsar-admin` is a CLI tool written in Java language for the Apache Pulsar project.

# Prerequisites

> Pulsar client is required to be newer than 2.6.1 which will include the OAuth2 authentication plugin.

- Pulsar broker 2.7.0-742fc5c9b+
- Get the `WEB_SERVICE_URL` of your StreamNative Cloud Pulsar cluster: [How to get service URL](#How to get service URL)
- Get the `AUTH_PARAMS` of your StreamNative Cloud Pulsar cluster: [How to get token options](#How to get token options)

# Usage

## Token

The `pulsar-admin` supports to connect to Pulsar cluster through Token, as shown below:

```shell script
./bin/pulsar-admin \
    --url WEB_SERVICE_URL \
    --auth-plugin org.apache.pulsar.client.impl.auth.AuthenticationToken \
    --auth-params token:AUTH_PARAMS \
    tenants list
```

Output:

```text
"public"
"pulsar"
```

## OAuth2

The `pulsar-admin` supports to connect to Pulsar cluster through OAuth2, as shown below:

```shell script
bin/pulsar-admin --admin-url https://cloud.streamnative.dev:443 \
  --auth-plugin org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2 \
  --auth-params '{"privateKey":"file:///path/to/key/file.txt",
    "issuerUrl":"https://test.auth0.com",
    "audience":"urn:sn:pulsar:test-pulsar-instance-namespace:test-pulsar-instance"}' \
  tenants list
```

Output:

```text
"public"
"pulsar"
```
