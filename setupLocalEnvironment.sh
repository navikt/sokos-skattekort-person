#!/bin/bash

# Ensure user is authenicated, and run login if not.
gcloud auth print-identity-token &> /dev/null
if [ $? -gt 0 ]; then
    gcloud auth login
fi
kubectl config use-context dev-fss
kubectl config set-context --current --namespace=okonomi

# Get AZURE and DATABASE system variables
envValue=$(kubectl exec -it $(kubectl get pods | grep sokos-skattekort-person | cut -f1 -d' ') -c sokos-skattekort-person -- env | egrep "^AZURE|^DATABASE")

# Set AZURE as local environment variables
rm -f defaults.properties
echo "$envValue" > defaults.properties
echo "AZURE and DATABASE env variables stores as defaults.properties"