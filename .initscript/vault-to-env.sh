#!/bin/sh

if test -f '/var/run/secrets/nais.io/database-user/username'; then
    export DATABASE_USERNAME=$(cat /var/run/secrets/nais.io/database-user/username)
    echo '- exporting DATABASE_USERNAME'
fi

if test -f '/var/run/secrets/nais.io/database-user/password'; then
    export DATABASE_PASSWORD=$(cat /var/run/secrets/nais.io/database-user/password)
    echo '- exporting DATABASE_PASSWORD'
fi