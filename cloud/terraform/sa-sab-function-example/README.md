# Terraform Provider streamnative/streamnative & streamnative/pulsar to create Pulsar Function on StreamNative Cloud

This repository contains the Terraform configuration to create a Pulsar Function on StreamNative Cloud with the Service Account and OAuth2.
The configuration includes two parts:
1. Manage Service Account, Service Account Binding using the `streamnative/streamnative` provider. It output the oauth2 configuration of the created Service Account so it could be used for the `streamnative/pulsar` provider, and makes the Pulsar Function to be able to run as the Service Account.
2. Manage Pulsar Function using the `streamnative/pulsar` provider.

## Prerequisites
- [Terraform](https://learn.hashicorp.com/tutorials/terraform/install-cli) installed
- [StreamNative Cloud](https://streamnative.io/cloud/) account, a running Pulsar Cluster
- StreamNative Cloud Service Account to the running Pulsar Cluster

## Usage
1. Config the `streamnative/streamnative` provider in `streamnative-cloud` folder.
   a. download the service account key from StreamNative Cloud Console, save it as `service-account-key.json` in `streamnative-cloud` folder.
2. Review and modify the `streamnative-cloud/main.tf` file to match your StreamNative Cloud and Pulsar Cluster configuration.
3. Review and modify the `pulsar/main.tf` file to match your Pulsar Function configuration.
4. Execute `apply.sh` to create the Service Account, Service Account Binding, and the Pulsar Function.