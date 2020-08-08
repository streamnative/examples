# Overview

The `pulsarctl` is a CLI tool written by Golang language for the Apache Pulsar project.

# Prerequisites

- pulsarctl 0.5.0+
- Get the `webServiceURL` of your StreamNative Cloud Pulsar cluster: [How to get service URL](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls)
- Get the OAuth2 `privateKeyFile` of a service account to access your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters)
- Get the `audience` of the your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters)
- Get the `issuerUrl` of the your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters)
- Get the `clientId` of the your StreamNative Cloud Pulsar cluster: [How to get OAuth2 options](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters)

More information reference to [here](https://github.com/streamnative/pulsarctl/blob/master/README.md).

# Usage

## Connect to cluster through Token

The `pulsarctl` supports to connect to Pulsar cluster through Token, the example as follows:

```shell script
pulsarctl \
    --admin-service-url WEB_SERVICE_URL \
    --token AUTH_PARAMS \
    tenants list
```

For details about how to get the `WEB_SERVICE_URL` and `AUTH_PARAMS` fields, please reference to **how to get Token options**.

## Connect to cluster through OAuth2

The `pulsarctl` supports to connect to Pulsar cluster through OAuth2, provide the following two ways:

### Use CLI Tool

The `pulsarctl` supports to connect to Pulsar cluster through OAuth2, as shown below:

1. Activate an account with keyfile.

```shell script
$ pulsarctl oauth2 activate \
    --issuer-endpoint https://cloud.streamnative.dev \
    --client-id abcdefghigk0123456789 \
    --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name \
    --key-file /path/to/private/key/file.json
```

Output:

```text
Logged in as cloud@streamnative.dev.
Welcome to Pulsar!
```

2. Using pulsarctl to get pulsar resources.

```shell script
$ pulsarctl namespaces list public \
    --admin-service-url https://cloud.streamnative.dev:443 \
    --issuer-endpoint https://cloud.streamnative.dev \
    --client-id abcdefghigk0123456789 \
    --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name \
    --key-file /path/to/private/key/file.json
```

Output:

```text
+----------------+
| NAMESPACE NAME |
+----------------+
| public/default |
+----------------+
```

### Use Go Admin API

The `pulsarctl` itself provides the function of the Go Admin API. You can use the interface of the Admin API to use the function of OAuth2 to establish a connection with the Pulsar cluster. 

```shell script
$ go build pulsarctl pulsarctl.go
$ ./pulsarctl -webServiceURL https://cloud.streamnative.dev:443 \
         -privateKeyFile /path/to/private/key/file.json\
         -audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name\
         -issuerUrl https://cloud.streamnative.dev\
         -clientId abcdefghigk0123456789
```

Output:

```text
the namespace is: [public/default]
```
