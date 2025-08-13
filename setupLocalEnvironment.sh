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
ORACLE_OSESKATT_U4=$(vault kv get -field=data oracle/dev/creds/oseskatt_read_u4-user)


# Set AZURE as local environment variables
rm -f defaults.properties
echo "$envValue" > defaults.properties
echo "env variables stores as defaults.properties"

username=$(echo "$ORACLE_OSESKATT_U4" | awk -F 'username:' '{print $2}' | awk '{print $1}' | sed 's/]$//')
password=$(echo "$ORACLE_OSESKATT_U4" | awk -F 'password:' '{print $2}' | awk '{print $1}' | sed 's/]$//')
echo "DATABASE_USERNAME=$username" >> defaults.properties
echo "DATABASE_PASSWORD=$password" >> defaults.properties