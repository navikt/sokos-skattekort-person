#!/bin/sh

if test -f '/var/run/secrets/nais.io/database-user/password'; then
    export DATABASE_PASSWORD=$(cat /var/run/secrets/nais.io/database-user/password)
    echo '- exporting DATABASE_PASSWORD'
fi