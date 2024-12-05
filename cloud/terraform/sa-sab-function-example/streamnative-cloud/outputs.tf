output "web_service_url" {
  value = data.streamnative_pulsar_cluster.cluster.http_tls_service_url
}

output "oauth2_audience" {
  value = data.streamnative_pulsar_instance.instance.oauth2_audience
}

output "oauth2_issuer_url" {
  value = data.streamnative_pulsar_instance.instance.oauth2_issuer_url
}

output "service_account_private_key_data" {
  value = streamnative_service_account.tf-func-runner.private_key_data
}

output "client_id" {
  value = local.service_account_private_key_data.client_id
}