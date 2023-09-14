#!/bin/bash

export VAULT_ADDR=https://vault.adeo.no

# Ensure user is authenicated, and run login if not.
gcloud auth print-identity-token &> /dev/null
if [ $? -gt 0 ]; then
    gcloud auth login
fi
kubectl config use-context dev-fss
kubectl config set-context --current --namespace=okonomi

# Get database username and password secret from Vault
[[ "$(vault token lookup -format=json | jq '.data.display_name' -r; exit ${PIPESTATUS[0]})" =~ "nav.no" ]] &>/dev/null || vault login -method=oidc -no-print
DATABASE_USERNAME=$(vault kv get -field=username oracle/dev/creds/oseskatt_read_u4-user)
DATABASE_PASSWORD=$(vault kv get -field=password oracle/dev/creds/oseskatt_read_u4-user)

# Get AZURE and DATABASE system variables
envValue=$(kubectl exec -it $(kubectl get pods | grep sokos-skattekort-person | cut -f1 -d' ') -c sokos-skattekort-person -- env | egrep "^AZURE|^DATABASE")

# Set AZURE as local environment variables
rm -f defaults.properties
echo "$envValue" > defaults.properties
echo "DATABASE_USERNAME=$DATABASE_USERNAME" >> defaults.properties
echo "DATABASE_PASSWORD=$DATABASE_PASSWORD" >> defaults.properties
echo "AZURE and DATABASE env variables stores as defaults.properties"