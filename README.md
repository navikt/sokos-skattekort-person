# sokos-skattekort-person

# Innholdsoversikt

* [1. Funksjonelle krav](#1-funksjonelle-krav)
* [2. Utviklingsmiljø](#2-utviklingsmiljø)
* [3. Programvarearkitektur](#3-programvarearkitektur)
* [4. Deployment](#4-deployment)
* [5. Autentisering](#5-autentisering)
* [6. Drift og støtte](#6-drift-og-støtte)
* [7. Swagger](#7-swagger)
* [8. Henvendelser og tilgang](#8-henvendelser-og-tilgang)

---

# 1. Funksjonelle Krav

Applikasjonen er en tjeneste som tilbyr uthenting av skattekort for personer som idag får ytelser fra NAV.
Per dags dato er det kun skattekort for 2022-2023 som er tilgjengelig.
Ved å kalle på rest tjenesten gjør applikasjonen et oppslag i databasen (med KUN lese rettigheter) som eies av
os-eskatt.

# 2. Utviklingsmiljø

### Forutsetninger

* Java 17
* Gradle
* [Kotest Plugin](https://plugins.jetbrains.com/plugin/14080-kotest) for å kjøre tester

### Bygge prosjekt

`./gradlew build`

### Lokal utvikling

For å kjøre applikasjonen må du gjøre følgende:

- Kjør scriptet [setupLocalEnvironment.sh](setupLocalEnvironment.sh)
     ```
     chmod 755 setupLocalEnvironment.sh && ./setupLocalEnvironment.sh
     ```
  Denne vil opprette [default.properties](defaults.properties) med alle environment variabler du trenger for å kjøre
  applikasjonen som er definert
  i [PropertiesConfig](src/main/kotlin/no/nav/sokos/skattekort.person/config/PropertiesConfig.kt).
  Her vil du også kunne f.eks endre om du ønsker slå på autentisering eller ikke i
  koden `"USE_AUTHENTICATION" to "true"` i
  filen [PropertiesConfig](src/main/kotlin/no/nav/sokos/skattekort.person/config/PropertiesConfig.kt).
  Husk å endre
- `DATABASE_HOST=a01dbfl032.adeo.no` til `DATABASE_HOST=10.51.9.59`
- `DATABASE_NAME=oseskatt_q1` til `DATABASE_NAME=oseskatt_u4`
- `DATABASE_SCHEMA=OSESKATT_P` til `DATABASE_SCHEMA=oseskatt_u4`
- `oseskatt_u4` databasen fordi dette er eneste databasen som kan nåes
  via [naisdevice](https://docs.nais.io/device/?h=naisdevice)

# 3. Programvarearkitektur

[System diagram](./dokumentasjon/system-diagram.md)

# 4. Deployment

Distribusjon av tjenesten er gjort med bruk av Github Actions.
[sokos-skattekort-person CI / CD](https://github.com/navikt/sokos-skattekort-person/actions)

Push/merge til master branche vil teste, bygge og deploye til produksjonsmiljø og testmiljø.

# 5. Autentisering

Applikasjonen bruker [AzureAD](https://docs.nais.io/security/auth/azure-ad/) autentisering

### Hente token

1. Installer `vault` kommandolinje verktøy: https://github.com/navikt/utvikling/blob/main/docs/teknisk/Vault.md
2. Installer `jq` kommandolinje verktøy: https://github.com/stedolan/jq
3. Gi rettighet for å kjøre scriptet `chmod 755 getAzureAdToken.sh`
4. Kjør scriptet [getAzureAdToken.sh](getAzureAdToken.sh)
      ```
      chmod 755 getAzureAdToken.sh && ./getAzureAdToken.sh
      ```
   Scriptet sørger for at vi kan hente token slik at applikasjonen kan kalle seg selv

# 6. Drift og støtte

### Logging

Vi logger til logs.adeo.no.

For å se på logger må man logge seg på logs.adeo.no og velge NAV logs.

Feilmeldinger og infomeldinger som ikke innheholder sensitive data logges til indeksen `logstash-apps`, mens meldinger
som inneholder sensitive data logges til indeksen `tjenestekall`.

- Filter for Produksjon
    * application:sokos-skattekort-person AND envclass:p

- Filter for Dev
    * application:sokos-skattekort-person AND envclass:q

[sikker-utvikling/logging](https://sikkerhet.nav.no/docs/sikker-utvikling/logging) - Anbefales å lese

- Filter for sikkerhet logs på https://logs.adeo.no
    * Bytte Change index pattern fra: `logstash-*` til: `tjenestekall-*`
    * Bruk samme filter for dev og prod som er vist over

### Kubectl

For dev-fss:

```shell script
kubectl config use-context dev-fss
kubectl get pods -n okonomi | grep sokos-skattekort-person
kubectl logs -f sokos-skattekort-person-<POD-ID> --namespace okonomi -c sokos-skattekort-person
```

For prod-fss:

```shell script
kubectl config use-context prod-fss
kubectl get pods -n okonomi | grep sokos-skattekort-person
kubectl logs -f sokos-skattekort-person-<POD-ID> --namespace okonomi -c sokos-skattekort-person
```

### Alarmer

Vi bruker [nais-alerts](https://doc.nais.io/observability/alerts) for å sette opp alarmer. Disse finner man konfigurert
i

- [Prod-miljø](.nais/alerts.yaml)

### Grafana

- [sokos-skattekort-person dashboard](https://grafana.nais.io/d/JC31xmTVz/sokos-skattekort-person?orgId=1&refresh=30s&from=now-24h&to=now)

---

# 7. Swagger

- [Prod-gcp](https://sokos-skattekort-person.intern.nav.no/api/v1/docs)
- [Dev-gcp](https://sokos-skattekort-person.dev.intern.nav.no/api/v1/docs)
- [Lokalt](http://0.0.0.0:8080/api/v1/docs)

# 8. Henvendelser og tilgang

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på Github.\
Interne henvendelser kan sendes via Slack i kanalen `#po-utbetaling`\
Tilgang til denne tjenesten kan bestilles gjennom [Porten](https://jira.adeo.no/plugins/servlet/desk/portal/541)



