## Terraform Serverless Example

This example demonstrates how to use [StreamNative Terraform Provider](https://registry.terraform.io/providers/streamnative/streamnative/latest/docs) to provision a Serverless cluster and use the [Pulsar Terraform Provider](https://registry.terraform.io/providers/streamnative/pulsar/latest/docs) to provision Pulsar resources in the Serverless cluster.

This tutorial basically provisions the following resources:

1. Provisions a Serverless Instance `sl-instance`.
2. Provisions a Serverless Cluster `sl-clu`.
3. Provisions a Service Account `sample-app-sa`.
4. Provisions an API Key for the Service Account `sample-app-apikey`.
5. Provisions a Pulsar Tenant `sample-app-tenant`
6. Provisions a Pulsar Namespace `sample-app-ns` and grants `produce` and `consume` permissions to the Service Account on the namespace.
7. Provisions a Pulsar Topic with 4 partitions `sample-app-topic`.
8. After provisioning all the resources, we will verify the resources by using the [pulsarctl](https://docs.streamnative.io/docs/pulsarctl-overview) command.

## 0. Prerequisites

- Install [Terraform](https://developer.hashicorp.com/terraform/install)
- Install [Pulsarctl](https://docs.streamnative.io/docs/pulsarctl-overview)

## 1. Clone this repository

```bash
git clone https://github.com/streamnative/examples.git
cd examples/terraform/serverless
```

## 2. Create a Super-Admin Service Account

First, you need to create a service account called `tf-runner` with **Super Admin** access. Please refer to [Create a Service Account](https://docs.streamnative.io/docs/service-accounts#create-a-service-account) for details.

After you have created the service account, download the OAuth2 credentials file and save it as `tf-runner.json` in `examples/terraform/serverless` folder.

## 3. Run Terraform Commands

You need to expose the `org_id` variable to the Terraform commands. Please replace `<your-org-id>` with your actual organization id.

```bash
export TF_VAR_org_id=<your-org-id>
```

First, initialize the Terraform working directory.

```bash
terraform init
```

Secondly, validate the Terraform configuration files.

```bash
terraform validate
```

Since we use two providers in this example (the **StreamNative Provider** and the **Pulsar Provider**), we need to provision the resources in two steps. The Pulsar Provider resources depend on the StreamNative Provider resources being created first.

### 3.1 Provision the Cloud Resources

Run a targeted plan to see the changes and preview the resources that will be created.

```bash
terraform plan --target=module.streamnative_cloud.streamnative_apikey.app-apikey
```

After that, run a targeted apply to create the resources.

```bash
terraform apply --target=module.streamnative_cloud.streamnative_apikey.app-apikey
```

### 3.2 Provision all the Resources

Run `terraform plan` to see the changes and preview the resources that will be created.

```bash
terraform plan
```

After that, run `terraform apply` to create the resources.

```bash
terraform apply
```

You should see a similar output as follows:

```bash
apikey = "<...>"
pulsarctl_command = "pulsarctl context set -s <pulsar-web-service-url> --token <apikey> sl-clu && pulsarctl topics get sample-app-tenant/sample-app-ns/sample-app-topic"
service_urls = {
  "kafka_bootstrap_url" = "..."
  "pulsar_broker_service_url" = "..."
  "pulsar_web_service_url" = "..."
}
```

## 5. Verify the Resources

Use the [pulsarctl](https://docs.streamnative.io/docs/pulsarctl-overview) command to verify all the resources created. Make sure you have installed pulsarctl before running the command.

Copy the `pulsarctl_command` output and run it in your terminal.

```bash
pulsarctl context set -s <pulsar-web-service-url> --token <apikey> sl-clu && pulsarctl topics get sample-app-tenant/sample-app-ns/sample-app-topic
```

This command will set the context and get the topic details. It will verify the following resources are created:

- A Pulsar cluster named `sl-clu`
- A Pulsar tenant named `sample-app-tenant`
- A Pulsar namespace named `sample-app-ns`
- A Pulsar topic named `sample-app-topic`
- `produce` and `consume` permissions are granted to `sample-app-sa` on the namespace `sample-app-tenant/sample-app-ns`

You should see the output as follows:

```bash
{
  "partitions": 4
}
```
