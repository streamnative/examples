provider "streamnative" {
  key_file_path = "./service-account-key.json"
}

locals {
  // base64 decode the content from resource.streamnative_service_account.tf-func-runner.private_key_data
  service_account_private_key_data = jsondecode(base64decode(resource.streamnative_service_account.tf-func-runner.private_key_data))
  instance_name = "PLACEHOLDER"
  cluster_name  = "PLACEHOLDER"
  organization = "PLACEHOLDER"
  function_runner_sa_name = "PLACEHOLDER"
}

data "streamnative_pulsar_instance" "instance" {
  name         = local.instance_name
  organization = local.organization
}

data "streamnative_pulsar_cluster" "cluster" {
  name         = local.cluster_name
  organization = local.organization
}

resource "streamnative_service_account" "sa" {
  name         = local.function_runner_sa_name
  organization = local.organization
  admin        = true
}

resource "streamnative_service_account_binding" "sa" {
  organization         = local.organization
  service_account_name = streamnative_service_account.sa.name
  cluster_name         = data.streamnative_pulsar_cluster.cluster.name

  depends_on = [resource.streamnative_service_account.sa]
}
