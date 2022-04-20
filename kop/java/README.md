# Overview

This document describes how to produce messages to and consume messages from a KoP cluster using the Kafka client.

# Prerequisites

- Java 1.8 or higher version
- Maven

> **NOTE**
>
> This example uses Kafka client 2.0.0. If you want to use another version of Kafka client, you can change the `kafka.version` property in `pom.xml` file. Kafka client version 1.0.0 - 2.6.0 are supported.

# Example

## Example: Token authentication 

See [KoP Security](https://github.com/streamnative/kop/blob/master/docs/security.md) for how to configure KoP with token authentication. This example takes a topic named `my-topic` under `public/default` namespace as reference.

1. Grant produce and consume permissions to the specific role.

   ```bash
   bin/pulsar-admin namespaces grant-permission public/default \
     --role <role> \
     --actions produce,consume
   ```

   > **NOTE**
   >
   > The `conf/client.conf` should be configured. For details, see [Configure CLI Tools](http://pulsar.apache.org/docs/en/security-jwt/#cli-tools).

2. Configure the token in [token.properties](src/main/resources/token.properties).

   ```properties
   topic=persistent://public/default/my-topic
   namespace=public/default
   token=token:<token-of-the-role>
   ```

3. Compile the project.

   ```
   mvn clean compile
   ```

4. Run a Kafka producer to produce a `hello` message.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.TokenProducer
   ```

   **Output:**

   ```
   Send hello to persistent://public/default/my-topic-0@0
   ```

5. Run a Kafka consumer to consume some messages.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.TokenConsumer
   ```

   **Output:**

   ```
   Receive record: hello from persistent://public/default/my-topic-0@0
   ```

## Example: OAuth2 authentication 

See [KoP Security](https://github.com/streamnative/kop/blob/master/docs/security.md#oauthbearer) for how to configure KoP with OAuth authentication. This example takes a topic named `my-topic` under `public/default` namespace as reference.
1. Start the Hydra OAuth2 server.

Start the Hydra OAuth2 server.
```shell
docker-compose -f $(git rev-parse --show-toplevel)/kop/java/src/main/resources/hydra/docker-compose.yml up -d
```
Initialize the Hydra OAuth2 server.
```shell
./$(git rev-parse --show-toplevel)/kop/java/src/main/resources/init_hydra_oauth_server.sh
```

2. Configure the oauth in Pulsar

This example will use the follow values:
> **Note**
>
> You need to replace the `privateKey` value with your local path to your `credentials_file.json` file.
```properties
# Enable the authentication
authenticationEnabled=true
authenticationProviders=org.apache.pulsar.broker.authentication.AuthenticationProviderToken
superUserRoles=simple_client_id
brokerClientAuthenticationPlugin=org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2
brokerClientAuthenticationParameters={"type":"client_credentials","privateKey":"file:///path/to/simple_credentials_file.json","issuerUrl":"http://localhost:4444","audience":"http://example.com/api/v2/"}
tokenPublicKey=data:;base64,MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA4g8rgGslfLNGdfh94KbfsMPjgX17nnEHnCLhrlVyA+jxSThiQyQVQCkZfav9k4cLCiKdoqxKtLV0RA3hWXGHE0qUNUJWVN3vz3NOI7ccEHBJHzbDk24NYxsW7M6zNfBfTc6ZrJr5XENy7emscODn8HJ2Qf1UkMUeze5EirJ2lsB9Zzo1GIw9ZU65W9HWWcgS5sL9eHlDRbVLmgph7jRzkQJGm2hOeyiE+ufUOWkBQH49BhKaNGfjZ8BOJ1WRsbIIVtwhS7m+HSIKmglboG+onNd5LYAmngbkCuhwjJajBQayxkeBeumvRQACC1+mKC5KaW40JmVRKFFHDcf892t6GX6c7PaVWPqvf2l6nYRbYT9nl4fQK1aUTiCqrPf2+WjEH1JIEwTfFZKTwpTtlr3ejGJMT7wH2L4uFbpguKawTo4lYHWN3IsryDfUVvNbb7l8KMqiuDIy+5R6WezajsCYI/GzvLGCYO1EnRTDFdEmipfbNT2/D91OPKNGmZLVUkVVlL0z+1iQtwfRamn2oRNHzMYMAplGikxrQld/IPUIbKjgtLWPDnfskoWvuCIDQdRzMpxAXa3O/cq5uQRpu2o8xZ8RYWixxrIGc1/8m+QQLy7DwcmVd0dGU29S+fnfOzWr43KWlyWfGsBLFxUkltjY6gx6oB6tsQVC3Cy5Eku8FdcCAwEAAQ==

# Use the KoP's built-in handler
kopOauth2AuthenticateCallbackHandler=io.streamnative.pulsar.handlers.kop.security.oauth.OauthValidatorCallbackHandler

# Java property configuration file of OauthValidatorCallbackHandler
kopOauth2ConfigFile=conf/kop-handler.properties

# Enable the authorization for test
authorizationEnabled=true
authorizationProvider=org.apache.pulsar.broker.authorization.PulsarAuthorizationProvider
```

3. Create a new OAuth2 client.

```shell
docker run --rm \
  --network hydra_default \
  oryd/hydra:v1.11.7 \
  clients create \
    --endpoint http://hydra:4445 \
    --id test_role \
    --secret test_secret \
    --grant-types client_credentials \
    --response-types token,code \
    --token-endpoint-auth-method client_secret_post \
    --audience http://example.com/api/v2/
```

4. Create a credentials file json named `credentials.json`

```properties
{
  "client_id":"test_role",
  "client_secret":"test_secret"
}

```

5. Grant produce and consume permissions to the specific role.

   ```bash
   bin/pulsar-admin namespaces grant-permission public/default \
     --role test_role \
     --actions produce,consume
   ```

> **Note**
>
> The `conf/client.conf` should be configured. For details, see [Configure CLI Tools](http://pulsar.apache.org/docs/en/security-jwt/#cli-tools).

6. Configure OAuth2 authentication parameters in [oauth.properties](src/main/resources/oauth.properties).

   ```properties
   bootstrap.servers=localhost:9092
   topic=persistent://public/default/my-topic
   group=my-group
   issuerUrl=http://localhost:4444
   credentialsUrl=file:///path/to/credentials.json
   audience=http://example.com/api/v2/
   ```

7. Compile the project.

   ```
   mvn clean compile
   ```

8. Run a Kafka producer to produce a `hello` message.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.OAuthProducer
   ```

   **Output:**

   ```
   Send hello to persistent://public/default/my-topic-0@0
   ```

9. Run a Kafka consumer to consume some messages.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.OAuthConsumer
   ```

   **Output:**

   ```
   Receive record: hello from persistent://public/default/my-topic-0@0
   ```
   
10. Stop the Hydra OAuth2 server.

```shell
docker-compose -f $(git rev-parse --show-toplevel)/kop/java/src/main/resources/hydra/docker-compose.yml down
```

11. Stop the Pulsar server.