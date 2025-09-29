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

# Get AZURE system variables
envValue=$(kubectl exec -it $(kubectl get pods | grep sokos-skattekort-person | cut -f1 -d' ') -c sokos-skattekort-person -- env | egrep "^AZURE|^PDL" | sort)

DATABASE_USER=$(vault kv get -field=data oracle/dev/creds/oseskatt_read_u4-user)
DATABASE_USERNAME=$(echo "$DATABASE_USER" | awk -F 'username:' '{print $2}' | awk '{print $1}' | sed 's/]$//')
DATABASE_PASSWORD=$(echo "$DATABASE_USER" | awk -F 'password:' '{print $2}' | awk '{print $1}' | sed 's/]$//')

# Set AZURE as local environment variables
rm -f defaults.properties
echo "$envValue" > defaults.properties
echo "env variables stores as defaults.properties"

echo "DATABASE_USERNAME=$DATABASE_USERNAME" >> defaults.properties
echo "DATABASE_PASSWORD=$DATABASE_PASSWORD" >> defaults.properties