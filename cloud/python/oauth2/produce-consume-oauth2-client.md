# Produce and consume messages using Oauth2 authentication plugin

This example demonstrates how to use the Oauth2 authentication plugin to connect to the Pulsar service for producing and consuming messages.

## Prerequisites

- Python 3 or higher
- pulsar-client 2.6.2 or higher

## Examples

- [OAuth2 Producer](./OAuth2Producer.py)
- [OAuth2 Consumer](./OAuth2Consumer.py)

## Steps

1. Get the service URLs. For details, see [Get Pulsar service URLs](https://github.com/streamnative/pulsar-examples/tree/master/cloud#get-pulsar-service-urls).

2. Get the oauth2 authentication parameters. For details, see [Get Oauth2 authentication parameters](https://github.com/streamnative/examples/tree/master/cloud#get-oauth2-authentication-parameters).

3. Run the Python consumer example to receive messages from the topic `oauth2`.

    ```shell
    python3 OAuth2Consumer.py \
        -su "pulsar+ssl://streamnative.cloud:6651 \
        -t oauth2 -n 10 --auth-params '{
        "issuer_url": "https://auth.streamnative.cloud",
        "private_key": "/path/to/private.key",
        "audience": "urn:sn:pulsar:test-organization-name:test-pulsar-instance-name"}'
    ```

4. Run the Python producer to publish messages to the topic `oauth2`.

    ```shell
    python3 OAuth2Producer.py \
        -su "pulsar+ssl://streamnative.cloud:6651 \
        -t oauth2 -n 10 --auth-params '{
        "issuer_url": "https://auth.streamnative.cloud",
        "private_key": "/path/to/private.key",
        "audience": "urn:sn:pulsar:test-organization-name:test-pulsar-instance-name"}'
    }'
    ```
    
    **Output**
    
    ```shell
    Produce message 'message 0 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 1 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 2 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 3 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 4 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 5 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 6 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 7 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 8 from oauth2 producer' to the pulsar service successfully.
    Produce message 'message 9 from oauth2 producer' to the pulsar service successfully.
    ```
