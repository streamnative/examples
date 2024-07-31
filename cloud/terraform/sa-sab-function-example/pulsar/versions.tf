terraform {
  required_version = ">= 1.3"

  required_providers {
    pulsar = {
      source  = "registry.terraform.io/streamnative/pulsar"
      version = "~> 0.3"
    }
  }
}