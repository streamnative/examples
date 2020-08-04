# Overview

The `pulsarctl` is a CLI tool written by Golang language for the Apache Pulsar project.

# Prerequisites

- pulsarctl 0.5.0+

More information reference to [here](https://github.com/streamnative/pulsarctl/blob/master/README.md).

# Usage

## Connect By Token

The `pulsarctl` supports to connect to Pulsar cluster through Token, the example as follows:

```shell script
pulsarctl \
    --admin-service-url WEB_SERVICE_URL \
    --token AUTH_PARAMS \
    tenants list
```

How to get the `WEB_SERVICE_URL` and `AUTH_PARAMS` fields, please reference to **how to get Token options**.

## Connect By OAuth2

The `pulsarctl` supports to connect to Pulsar cluster through OAuth2, provide the following two ways:

### Use CLI Tool

The `pulsarctl` supports to connect to Pulsar cluster through OAuth2, the example as follows:

1. Activate an account with keyfile.

```shell script
$ pulsarctl oauth2 activate \
  --issuer-endpoint https://oauth.service \
  --client-id 0Xx...hyYyxeny \
  --audience audience-path \
  --key-file /path/to/private/key
```

2. Using pulsarctl to get pulsar resources.

```shell script
$ pulsarctl namespaces list public \
  --admin-service-url http://pulsar.service \
  --issuer-endpoint https://oatuh.service/ \
  --client-id 0Xx...hyYyxeny \
  --audience audience-path \
  --key-file /path/to/private/key
```

### Use Go Admin API

The `pulsarctl` itself provides the function of the Go Admin API. You can use the interface of the Admin API to use the function of OAuth2 to establish a connection with the Pulsar cluster. 

How to get the `keyFile`, `IssuerEndpoint`, `Audience` and `ClientID` fields, please reference to **how to get OAuth2 options**.
