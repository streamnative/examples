# Overview

The `pulsarctl` is a CLI tool written by Golang language for the Apache Pulsar project.

# Prerequisites

- pulsarctl 0.5.0+

More information reference to [here](https://github.com/streamnative/pulsarctl/blob/master/README.md).

# Usage

The `pulsarctl` supports users to connect to Pulsar cluster through Token, the example as follows:

```shell script
pulsarctl \
    --admin-service-url WEB_SERVICE_URL \
    --token AUTH_PARAMS \
    tenants list
```

How to get the `WEB_SERVICE_URL` and `AUTH_PARAMS` fields, please reference to **how to get Token options**.
