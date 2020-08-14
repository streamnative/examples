# Overview

The `pulsarctl` is a CLI tool written by Golang language for the Apache Pulsar project.

# Prerequisites

- pulsarctl 0.5.0+

More information reference to [here](https://github.com/streamnative/pulsarctl/blob/master/README.md).

# Example

## Connect to cluster through OAuth2 authentication plugin

pulsarctl supports to connect to Pulsar cluster through the OAuth2 authentication plugin in the following two ways.

### Use CLI Tool

1. Get the Web service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Activate an account with Oauth2 key file.

    ```shell script
    pulsarctl oauth2 activate \
        --issuer-endpoint https://streamnative.cloud \
        --client-id abcdefghigk0123456789 \
        --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name \
        --key-file /path/to/private/key/file.json
    ```

    **Output**:

    ```text
    Logged in as cloud@streamnative.dev.
    Welcome to Pulsar!
    ```

4. Use pulsarctl to get Pulsar resources.

    ```shell script
    pulsarctl namespaces list public \
        --admin-service-url https://streamnative.cloud:443 \
        --issuer-endpoint https://streamnative.cloud \
        --client-id abcdefghigk0123456789 \
        --audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name \
        --key-file /path/to/private/key/file.json
    ```

    **Output**:

    ```text
    +----------------+
    | NAMESPACE NAME |
    +----------------+
    | public/default |
    +----------------+
    ```

### Use Go Admin API

1. Get the Web service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Connect to the Pulsar cluster through the Oauth2 authentication plugin.

    ```shell script
    go build pulsarctl ConnectByOAuth2.go
    ./pulsarctl -webServiceURL https://streamnative.cloud:443 \
          -privateKeyFile /path/to/private/key/file.json\
          -audience urn:sn:pulsar:pulsar-instance-ns:pulsar-instance-name\
          -issuerUrl https://streamnative.cloud\
          -clientId abcdefghigk0123456789
    ```

    **Output**:

    ```text
    the namespace is: [public/default]
    ```

## Connect to cluster through Token authentication plugin

1. Get the Web service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Token authentication parameters. For details, see [Get Token authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-token-authentication-parameters).

3. Connect to the Pulsar cluster through the Token authentication plugin.

    ```shell script
    pulsarctl \
        --admin-service-url WEB_SERVICE_URL \
        --token AUTH_PARAMS \
        tenants list
    ```