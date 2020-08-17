# Overview

This directory includes examples of how Pulsar CLI tools and Pulsar clients connect to a Pulsar cluster through the OAuth2 or Token authentication plugin.

**Note**

> Currently, the Python and Node.js clients only support to connect to a cluster through the Token authentication plugin. Other Pulsar CLI tools and Pulsar clients support to connect to a cluster through Oauth2 or Token authentication plugin. 

- Supported Pulsar CLI tools
  - [pulsarctl](https://github.com/streamnative/pulsar-examples/tree/master/cloud/pulsarctl)
  - [pulsar-admin](https://github.com/streamnative/pulsar-examples/tree/master/cloud/pulsar-admin)
  - [pulsar-client](https://github.com/streamnative/pulsar-examples/tree/master/cloud/pulsar-client)
  - [pulsar-perf](https://github.com/streamnative/pulsar-examples/tree/master/cloud/pulsar-client)

- Supported Pulsar clients
  - [Java client](https://github.com/streamnative/pulsar-examples/tree/master/cloud/java)
  - [C++ client](https://github.com/streamnative/pulsar-examples/tree/master/cloud/cpp)
  - [Go client](https://github.com/streamnative/pulsar-examples/tree/master/cloud/go)
  - [Python client](https://github.com/streamnative/pulsar-examples/tree/master/cloud/python)
  - [Node.js client](https://github.com/streamnative/pulsar-examples/tree/master/cloud/node)

To use these tools or clients to connect to StreamNative Cloud, you need get the Pulsar service URLs of the StreamNative Cloud and OAuth2 or Token authentication parameters that are used to connect to the service URLs.

Before starting, you should already install snctl, and create the [service account](https://streamnative.io/docs/cloud/stable/managed-access/service-account.md#create-service-account-through-snctl), [Pulsar instance](https://streamnative.io/docs/cloud/stable/use/instance.md#create-instance-through-snctl), and [Pulsar cluster](https://streamnative.io/docs/cloud/stable/use/cluster.md#create-cluster-through-snctl). We take the following resources as examples.

| Item | Name | Namespace |
| --- | --- |--- |
| Pulsar instance | test-pulsar-instance-name | test-pulsar-instance-namespace |
| Pulsar cluster  | test-pulsar-cluster-name | test-pulsar-cluster-namespace |
| Service account | test-service-account-name | test-service-account-namespace |

# Get Pulsar service URLs

- `SERVICE_URL`: the Pulsar service URL for your cluster. A `SERVICE_URL` is a combination of the protocol, hostname and port ID.
- `WEB_SERVICE_URL`: Pulsar Web service URL for your cluster. A `WEB_SERVICE_URL` is a combination of the protocol, hostname and port ID.

For both the `SERVICE_URL` and  `WEB_SERVICE_URL`  parameters, you can use the following command to get the `hostname` value .

```shell script
$ snctl get pulsarclusters [PULSAR_CLUSTER_NAME] -n [PULSAT_CLUSTER_NAMESPACE] -o json | jq '.spec.serviceEndpoints[0].dnsName'
```

This example shows how to get the `hostname` value of the `SERVICE_URL` and  `WEB_SERVICE_URL` parameters.

```
snctl pulsarclusters get test-pulsar-cluster-name -n test-pulsar-cluster-namespace -o json | jq '.spec.serviceEndpoints[0].dnsName'
```

**Output**:

```text
streamnative.cloud
```

Here is an example of the service URL.

  ```
  pulsar+ssl://streamnative.cloud:6651
  ```

Here is an example of the Web service URL.

  ```
  https://streamnative.cloud:443
  ```

# Get Oauth2 authentication parameters

To connect to a Pulsar cluster through the Oauth2 authentication plugin, you should specify the following parameters.

- `type`: Oauth2 authentication type. Currently, this parameter can only be set to the `client_credentials`.
- `clientId`: client ID.
- `issuerUrl`: URL of the authentication provider which allows the Pulsar client to obtain an access token.
- `privateKey`: URL of a JSON credentials file.
- `audience`: The identifier for the Pulsar instance.

- For the `privateKey` parameter, you can use the following command to get the path of an Oauth2 key file.

    ```shell script
    snctl auth export-service-account [SERVICE_ACCOUNT_NAME] -n [SERVICE_ACCOUNT_NAMESPACE] [flags]
    
    Flags:
      -h, --help              help for export-service-account
      -f, --key-file string   Path to the private key file.
          --no-wait           Skip waiting for service account readiness.
    ```
    
    This example shows how to get the Oauth2 key file.
    
    ```
    snctl auth export-service-account test-service-account-name -n test-service-account-namespace -f [/path/to/key/file.json]
    ```
    
    **Output:**
    
    ```text
    Wrote private key file <Path of your private key file>
    ```

- For the `clientId` and `issuerUrl` parameters, you can get the corresponding value from the Oauth2 key file. Here is an example of the Oauth2 key file.

    ```text
    {
    "type":"sn_service_account",
    "client_id":"0123456789abcdefg",
    "client_secret":"ABCDEFG-JzAFKtj0Dcub9KF1WKN-qhFHBvgfAU_123456789-KI9",
    "client_email":"test@gtest.auth0.com",
    "issuer_url":"https://auth.streamnative.cloud"
    }
    ```

- For the `audience` parameter, it is a combination of the `urn:sn:pulsar`, as well as the namespace and the name of the Pulsar instance. Here is an example of the `audience` parameter.

    ```text
    urn:sn:pulsar:test-pulsar-instance-namespace:test-pulsar-instance-name
    ```

# Get Token authentication parameters

To connect to a Pulsar cluster through Token authentication plugin, you need to specify the `AUTH_PARAMS` option with the token you obtained through the following command.

```shell script
snctl auth get-token [PULSAR_INSTANCE_NAME] -n [PULSAR_INSTANCE_NAMESPACE] [flags]

Flags:
  -h, --help              help for get-token
  -f, --key-file string   Path to the private key file
      --login             Use an interactive login
      --skip-open         if the web browser should not be opened automatically
```

This example shows how to get a token.

```
snctl auth get-token test-pulsar-instance-name -n test-pulsar-instance-namespace --login
```

**Output:**

```text
We've launched your web browser to complete the login process.
Verification code: ABCD-EFGH

Waiting for login to complete...
Logged in as cloud@streamnative.io.
Welcome to Apache Pulsar!

Use the following access token to access Pulsar instance 'test-pulsar-instance-namespace/test-pulsar-instance-name':

abcdefghijklmnopqrstuiwxyz0123456789
```

**Tip**

> In code implementation, for safety and convenience, you can set `AUTH_PARAMS` as an environment variable.
