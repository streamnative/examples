terraform {
  required_providers {
    streamnative = {
      source  = "streamnative/streamnative"
      version = ">= 0.7.0"
    }
  }
}

provider "streamnative" {
  key_file_path = "./tf-runner.json"
}

data "streamnative_service_account" "tf-runner" {
  organization = var.org_id
  name = "tf-runner"
}

resource "streamnative_pulsar_instance" "serverless-instance" {
  organization = var.org_id
  name = var.instance_name
  availability_mode = "regional"
  pool_name = var.serverless_pool_name
  pool_namespace = "streamnative"
  type = "serverless"
}

data "streamnative_pulsar_instance" "serverless-instance" {
  depends_on = [streamnative_pulsar_instance.serverless-instance]
  name = streamnative_pulsar_instance.serverless-instance.name
  organization = streamnative_pulsar_instance.serverless-instance.organization
}

resource "streamnative_pulsar_cluster" "serverless-cluster" {
  depends_on = [streamnative_pulsar_instance.serverless-instance]
  organization    = streamnative_pulsar_instance.serverless-instance.organization
  display_name    = "serverless-cluster"
  instance_name   = streamnative_pulsar_instance.serverless-instance.name
  location        = var.region
}

data "streamnative_pulsar_cluster" "serverless-cluster" {
  depends_on   = [streamnative_pulsar_cluster.serverless-cluster]
  organization = streamnative_pulsar_cluster.serverless-cluster.organization
  name         = split("/", streamnative_pulsar_cluster.serverless-cluster.id)[1]
}

resource "streamnative_service_account" "app-sa" {
  organization = var.org_id
  name = "${var.app_name}-sa"
  admin = false
}

data "streamnative_service_account" "app-sa" {
  depends_on = [streamnative_service_account.app-sa]
  organization = streamnative_service_account.app-sa.organization
  name = streamnative_service_account.app-sa.name
}

resource "streamnative_apikey" "app-apikey" {
  depends_on = [streamnative_pulsar_cluster.serverless-cluster, streamnative_service_account.app-sa]
  organization = var.org_id
  name = "${var.app_name}-apikey"
  instance_name = streamnative_pulsar_instance.serverless-instance.name
  service_account_name = streamnative_service_account.app-sa.name
  description = "This is a test api key for ${var.app_name} in running the terraform tutorial"
  # If you don't want to set expiration time, you can set expiration_time to "0"
  # expiration_time = "2025-01-01T10:00:00Z"
  expiration_time = "0"
}

data "streamnative_apikey" "app-apikey" {
  depends_on = [streamnative_apikey.app-apikey]
  organization = streamnative_apikey.app-apikey.organization
  name = streamnative_apikey.app-apikey.name
  private_key = streamnative_apikey.app-apikey.private_key
}

output "apikey_token" {
  value = data.streamnative_apikey.app-apikey.token
}

output "pulsar_web_service_url" {
  value = data.streamnative_pulsar_cluster.serverless-cluster.http_tls_service_url
}

output "pulsar_cluster_name" {
  value = data.streamnative_pulsar_cluster.serverless-cluster.name
}

output "pulsar_instance_audience" {
  value = data.streamnative_pulsar_instance.serverless-instance.oauth2_audience
}

output "app_service_account_principal" {
  value = "${data.streamnative_service_account.app-sa.name}@${data.streamnative_service_account.app-sa.organization}.auth.streamnative.cloud"
}

output "service_urls" {
  value = {
    pulsar_web_service_url = data.streamnative_pulsar_cluster.serverless-cluster.http_tls_service_url
    pulsar_broker_service_url = data.streamnative_pulsar_cluster.serverless-cluster.pulsar_tls_service_url
    kafka_bootstrap_url = data.streamnative_pulsar_cluster.serverless-cluster.kafka_service_url
  }
}
