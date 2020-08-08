# Overview

The `pulsar-admin` is a CLI tool written in Java language for the Apache Pulsar project.

# Prerequisites

- Pulsar client 2.6.1 or higher version (only for connecting the Pulsar cluster through the OAuth2 authentication plugin).
- Pulsar broker 2.7.0-742fc5c9b+

# Example

This section describes how to connect to a Pulsar cluster through the Oauth2 authentication plugin or the Token authentication plugin.

## Connect to cluster through OAuth2 authentication plugin

1. Get the Web service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Connect to the Pulsar cluster through the Oauth2 authentication plugin.

    ```shell script
    bin/pulsar-admin --admin-url https://cloud.streamnative.dev:443 \
      --auth-plugin org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2 \
      --auth-params '{"privateKey":"file:///path/to/key/file.json",
        "issuerUrl":"https://test.auth0.com",
        "audience":"urn:sn:pulsar:test-pulsar-instance-namespace:test-pulsar-instance"}' \
      tenants list
    ```

    **Output**:

    ```text
    "public"
    "pulsar"
    ```

## Connect to cluster through Token authentication plugin

1. Get the Web service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the Token authentication parameters. For details, see [Get Token authentication parameters](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-token-authentication-parameters).

3. Connect to the Pulsar cluster through the Token authentication plugin.

    ```shell script
    ./bin/pulsar-admin \
        --url WEB_SERVICE_URL \
        --auth-plugin org.apache.pulsar.client.impl.auth.AuthenticationToken \
        --auth-params token:AUTH_PARAMS \
        tenants list
    ```

    **Output**:

    ```text
    "public"
    "pulsar"
    ```