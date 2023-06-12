#!/bin/bash

APP_NAME=''
ENVIRONMENT_NAME=''

echo '**** Get token from Azure ****'
echo
read -p 'Miljø (dev | prod): ' ENVIRONMENT_NAME
echo

# Gcloud authorized and switch to namespace okonomi
# Ensure user is authenicated, and run login if not.x
gcloud auth print-identity-token &> /dev/null
if [ $? -gt 0 ]; then
    gcloud auth login
fi
kubectl config use-context $ENVIRONMENT_NAME-fss
kubectl config set-context --current --namespace=okonomi

# Get secret from GCP
AZURE_APP_SERVER_ID=$(kubectl exec -it $(kubectl get pods | grep sokos-skattekort-person | cut -f1 -d' ') -c sokos-skattekort-person -- env | grep -E "AZURE_APP_CLIENT_ID" | cut -d "=" -f 2 | tr -d '\r')
AZURE_OPENID_CONFIG_TOKEN_ENDPOINT=$(kubectl exec -it $(kubectl get pods | grep sokos-skattekort-person | cut -f1 -d' ') -c sokos-skattekort-person -- env | grep -E "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT" | cut -d "=" -f 2 | tr -d '\r')

AZURE_APP_CLIENT_ID=$(kubectl exec -it $(kubectl get pods | grep sokos-skattekort-person | cut -f1 -d' ') -c sokos-skattekort-person -- env | grep -E "AZURE_APP_CLIENT_ID" | cut -d "=" -f 2 | tr -d '\r')
AZURE_APP_CLIENT_SECRET=$(kubectl exec -it $(kubectl get pods | grep sokos-skattekort-person | cut -f1 -d' ') -c sokos-skattekort-person -- env | grep -E "AZURE_APP_CLIENT_SECRET" | cut -d "=" -f 2 | tr -d '\r')

echo
echo "APP_NAME                              : sokos-skattekort-person"
echo "AZURE_APP_SERVER_ID                   : $AZURE_APP_SERVER_ID"
echo "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT    : $AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"
echo "AZURE_APP_CLIENT_ID                   : $AZURE_APP_CLIENT_ID"
echo "AZURE_APP_CLIENT_SECRET               : $AZURE_APP_CLIENT_SECRET"
echo

token=$(curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d "grant_type=client_credentials&client_id=$AZURE_APP_CLIENT_ID&scope=api://$AZURE_APP_SERVER_ID/.default&client_secret=$AZURE_APP_CLIENT_SECRET" "$AZURE_OPENID_CONFIG_TOKEN_ENDPOINT" | jq -r .access_token)
echo
echo "Access token: $token"
