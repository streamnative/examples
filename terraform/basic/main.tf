module "streamnative_cloud" {
  source = "./streamnative_cloud"
  org_id = var.org_id
  instance_name = var.instance_name
  cluster_name = var.cluster_name
  app_name = var.app_name
}

terraform {
  required_providers {
    pulsar = {
      source = "streamnative/pulsar"
    }
  }
}

provider "pulsar" {
  web_service_url = module.streamnative_cloud.pulsar_web_service_url
  key_file_path = "./tf-runner.json"
  audience = module.streamnative_cloud.pulsar_instance_audience
}

resource "pulsar_tenant" "app_tenant" {
  depends_on = [module.streamnative_cloud.depends_on]
  tenant = "${var.app_name}-tenant"
  allowed_clusters = [
    module.streamnative_cloud.pulsar_cluster_name,
  ]
}

resource "pulsar_namespace" "app_namespace" {
  depends_on = [pulsar_tenant.app_tenant, module.streamnative_cloud.depends_on]
  tenant    = pulsar_tenant.app_tenant.tenant
  namespace = "${var.app_name}-ns"

  namespace_config {
    replication_clusters = [
      module.streamnative_cloud.pulsar_cluster_name
    ]
    
  }
  permission_grant {
    role = module.streamnative_cloud.app_service_account_principal
    actions = ["produce", "consume"]
  }
}

resource "pulsar_topic" "app_topic" {
  depends_on = [pulsar_namespace.app_namespace, pulsar_tenant.app_tenant]
  tenant     = pulsar_tenant.app_tenant.tenant
  namespace  = pulsar_namespace.app_namespace.namespace
  topic_type = "persistent"
  topic_name = "${var.app_name}-topic"
  partitions =  4
}

output "apikey" {
  value = module.streamnative_cloud.apikey_token
}

output "pulsar_oauth2_audience" {
  value = module.streamnative_cloud.pulsar_instance_audience
}

output "service_urls" {
  value = module.streamnative_cloud.service_urls
}

output "pulsar_cluster_name" {
  value = module.streamnative_cloud.pulsar_cluster_name
}

output "pulsarctl_command" {
  value = "pulsarctl context set -s ${module.streamnative_cloud.pulsar_web_service_url} --token ${module.streamnative_cloud.apikey_token} ${module.streamnative_cloud.pulsar_cluster_name} && pulsarctl topics get ${var.app_name}-tenant/${var.app_name}-ns/${var.app_name}-topic"
}
