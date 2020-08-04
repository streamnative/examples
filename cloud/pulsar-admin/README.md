# Overview

The `pulsar-admin` is a CLI tool written by Java language for the Apache Pulsar project.

# Prerequisites

- Pulsar broker 2.7.0-742fc5c9b+

> You can get this tarball from [bintray](https://bintray.com/streamnative/maven/org.apache.pulsar/2.7.0-742fc5c9b). When the 2.6.1 release is complete, you can also use the official 2.6.1 version.

# Usage

The `pulsar-admin` supports to connect to Pulsar cluster through Token, the example as follows:

```shell script
./bin/pulsar-admin \
    --url WEB_SERVICE_URL \
    --auth-params AUTH_PARAMS \
    tenants list
```

How to get the `WEB_SERVICE_URL` and `AUTH_PARAMS` fields, please reference to **how to get Token options**.
