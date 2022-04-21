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

1. Configure the pulsar broker, this example will use the follow values:

> **Note**
>
> Need to change the `credentials.json` and `kop-handler-oauth2.properties` paths to your local path. The example file can be found in `src/main/resources/`.
> 
   ```properties
   # Enable KoP
   messagingProtocols=kafka
   protocolHandlerDirectory=./protocols
   allowAutoTopicCreationType=partitioned
   
   # Use `kafkaListeners` here for KoP 2.8.0 because `listeners` is marked as deprecated from KoP 2.8.0
   kafkaListeners=PLAINTEXT://127.0.0.1:9092
   # This config is not required unless you want to expose another address to the Kafka client.
   # If it’s not configured, it will be the same with `kafkaListeners` config by default
   kafkaAdvertisedListeners=PLAINTEXT://127.0.0.1:9092
   brokerEntryMetadataInterceptors=org.apache.pulsar.common.intercept.AppendIndexMetadataInterceptor
   
   brokerDeleteInactiveTopicsEnabled=false
   
   # Enable the authentication
   authenticationEnabled=true
   authenticationProviders=org.apache.pulsar.broker.authentication.AuthenticationProviderToken
   superUserRoles=Xd23RHsUnvUlP7wchjNYOaIfazgeHd9x@clients
   brokerClientAuthenticationPlugin=org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2
   brokerClientAuthenticationParameters={"type":"client_credentials","privateKey":"/path/to/credentials.json","issuerUrl":"https://dev-kt-aa9ne.us.auth0.com","audience":"https://dev-kt-aa9ne.us.auth0.com/api/v2/"}
   tokenPublicKey=data:;base64,MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2tZd/4gJda3U2Pc3tpgRAN7JPGWx/Gn17v/0IiZlNNRbP/Mmf0Vc6G1qsnaRaWNWOR+t6/a6ekFHJMikQ1N2X6yfz4UjMc8/G2FDPRmWjA+GURzARjVhxc/BBEYGoD0Kwvbq/u9CZm2QjlKrYaLfg3AeB09j0btNrDJ8rBsNzU6AuzChRvXj9IdcE/A/4N/UQ+S9cJ4UXP6NJbToLwajQ5km+CnxdGE6nfB7LWHvOFHjn9C2Rb9e37CFlmeKmIVFkagFM0gbmGOb6bnGI8Bp/VNGV0APef4YaBvBTqwoZ1Z4aDHy5eRxXfAMdtBkBupmBXqL6bpd15XRYUbu/7ck9QIDAQAB
   
   # Use the KoP's built-in handler
   kopOauth2AuthenticateCallbackHandler=io.streamnative.pulsar.handlers.kop.security.oauth.OauthValidatorCallbackHandler
   
   # Java property configuration file of OauthValidatorCallbackHandler
   kopOauth2ConfigFile=/path/to/kop-handler-oauth2.properties
   
   saslAllowedMechanisms=OAUTHBEARER
   ```


2. Configure the credentials in [credentials.json](src/main/resources/credentials.json).

   ```json
   {
   "client_id":"Xd23RHsUnvUlP7wchjNYOaIfazgeHd9x",
   "client_secret":"rT7ps7WY8uhdVuBTKWZkttwLdQotmdEliaM5rLfmgNibvqziZ-g07ZH52N_poGAb",
   "audience":"https://dev-kt-aa9ne.us.auth0.com/api/v2/",
   "grant_type":"client_credentials"
   }
   ```

3.Configure the oauth in [oauth.properties](src/main/resources/oauth.properties).

   ```properties
   bootstrap.servers=localhost:9092
   topic=persistent://public/default/my-topic
   group=my-group
   issuerUrl=https://dev-kt-aa9ne.us.auth0.com
   credentialsUrl=/path/to/credentials.json
   audience=https://dev-kt-aa9ne.us.auth0.com/api/v2/
   ```

4.Compile the project.

   ```
   mvn clean compile
   ```

5.Run a Kafka producer to produce a `hello` message.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.OAuthProducer
   ```

   **Output:**

   ```
   Send hello to persistent://public/default/my-topic-0@0
   ```

6. Run a Kafka consumer to consume some messages.

   ```bash
   mvn exec:java -Dexec.mainClass=io.streamnative.examples.kafka.OAuthConsumer
   ```

   **Output:**

   ```
   Receive record: hello from persistent://public/default/my-topic-0@0
   ```
