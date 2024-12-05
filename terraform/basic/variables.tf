variable "org_id" {
  type = string
}

variable "instance_name" {
  type = string
  default = "inst-001"
}

variable "cluster_name" {
  type = string
  default = "clu-001"
}

variable "app_name" {
  type = string
  default = "app001"
}

variable "serverless_pool_name" {
  type = string
  default = "shared-gcp"
}

variable "region" {
  type = string
  default = "us-central1"
}
