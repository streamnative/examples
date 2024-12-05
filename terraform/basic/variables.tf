variable "org_id" {
  type = string
}

variable "instance_name" {
  type = string
  default = "sl-instance"
}

variable "cluster_name" {
  type = string
  default = "sl-clu"
}

variable "app_name" {
  type = string
  default = "sample-app"
}

variable "serverless_pool_name" {
  type = string
  # specify the pool name of the serverless cluster
  # - GCP: "shared-gcp"
  # - AWS: "shared-aws"
  default = "shared-aws"
}

variable "region" {
  type = string
  # specify the region of the serverless cluster
  default = "us-east-2"
}
