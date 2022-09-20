# Spring Pulsar Integration Example

This is an example application demonstrating how to use [Spring for Apache Pulsar](https://github.com/spring-projects-experimental/spring-pulsar).

## Prerequisites

* [Java 17 or higher](https://jdk.java.net/17/) to run
* [Maven](https://maven.apache.org/) to compile
* [StreamNative Cloud](https://streamnative.io) instance or your own Pulsar cluster


If you are using StreamNative Cloud you need to configure [your Pulsar service URLs and authentication](https://docs.streamnative.io/cloud/stable/connect/overview) in the [application.yaml file](src/main/resources/application.yml) as detailed below:
```yaml
spring:
  pulsar:
    client:
      service-url: pulsar+ssl://free.o-j8r1u.snio.cloud:6651
      auth-plugin-class-name: org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2
      authentication:
        private-key: file:///Users/user/Downloads/o-j8r1u-free.json
        audience: urn:sn:pulsar:o-j8r1u:free
        issuer-url: https://auth.streamnative.cloud/
```

## Build the example

You can build the example application with the following command:

```bash
mvn clean install
```

## Run the example

You can run the example application with the following command:

```bash
mvn spring-boot:run
```
