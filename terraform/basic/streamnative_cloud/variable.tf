variable "org_id" {
  type = string
  # get your StreamNative Cloud organization id: https://docs.streamnative.io/docs/organizations#cloud-organization-id
  default = "o-lgohx"
}

variable "instance_name" {
  type = string
  default = "sl-instance"
}

variable "cluster_name" {
  type = string
  default = "sl-cluster"
}

variable "app_name" {
  type = string
  default = "sample-app"
}
