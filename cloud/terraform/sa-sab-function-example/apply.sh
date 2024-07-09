#!/bin/bash

# Navigate to streamnative configuration and apply it
cd ./streamnative-cloud
terraform init
terraform apply -auto-approve

# Extract the output values
WEB_SERVICE_URL=$(terraform output -raw web_service_url)
OAUTH2_AUDIENCE=$(terraform output -raw oauth2_audience)
OAUTH2_ISSUER_URL=$(terraform output -raw oauth2_issuer_url)
CLIENT_ID=$(terraform output -raw client_id)
SERVICE_ACCOUNT_PRIVATE_KEY_DATA=$(terraform output -raw service_account_private_key_data)

# Navigate to pulsar configuration directory
cd ../pulsar

# Create a terraform.tfvars file with the extracted variables
cat <<EOF > terraform.tfvars
web_service_url                   = "${WEB_SERVICE_URL}"
oauth2_audience                   = "${OAUTH2_AUDIENCE}"
oauth2_issuer_url                 = "${OAUTH2_ISSUER_URL}"
client_id                         = "${CLIENT_ID}"
service_account_private_key_data  = "${SERVICE_ACCOUNT_PRIVATE_KEY_DATA}"
EOF

# Apply the pulsar configuration
terraform init
terraform apply -auto-approve
