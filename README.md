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
Per dags dato er det kun skattekort for nåværende år +/- 1 år som er tilgjengelig.
Ved å kalle på rest tjenesten gjør applikasjonen et oppslag i databasen (med KUN lese rettigheter) som eies av
os-eskatt.

# 2. Utviklingsmiljø

### Forutsetninger

* Java 21
* Gradle
* [Kotest](https://plugins.jetbrains.com/plugin/14080-kotest) plugin for å kjøre Kotest tester

### Bygge prosjekt

`./gradlew build shadowJar`

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
- `DATABASE_HOST=a01dbfl032.adeo.no` til `DATABASE_HOST=10.51.9.59` (nåes gjennom naisdevice)
- `DATABASE_NAME=oseskatt_q1` til `DATABASE_NAME=oseskatt_u4`
- `DATABASE_SCHEMA=OSESKATT_P` til `DATABASE_SCHEMA=oseskatt_u4`
- `oseskatt_u4` databasen fordi dette er eneste databasen som kan nåes
  via [naisdevice](https://docs.nais.io/device/?h=naisdevice)

# 3. Programvarearkitektur

[System diagram](./dokumentasjon/system-diagram.md)

# 4. Deployment

Distribusjon av tjenesten er gjort med bruk av Github Actions.
[sokos-skattekort-person CI / CD](https://github.com/navikt/sokos-skattekort-person/actions)

Push/merge til main branch vil teste, bygge og deploye til produksjonsmiljø og testmiljø.

# 5. Autentisering

Applikasjonen bruker [AzureAD](https://docs.nais.io/security/auth/azure-ad/) autentisering

# 6. Drift og støtte

### Logging

Vi logger til https://logs.adeo.no/.

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

- [Prod-miljø](.nais/alerts-prod.yaml)
- [Dev-miljø](.nais/alerts-dev.yaml)

Disse dukker opp i `#team-mob-alerts-dev` og `#team-mob-alers-prod` kanalene på Slack

### Grafana

- [sokos-skattekort-person dashboard](https://grafana.nais.io/d/JC31xmTVz/sokos-skattekort-person?orgId=1&refresh=30s&from=now-24h&to=now)

---

# 7. Swagger

- [Prod-fss](https://sokos-skattekort-person.intern.nav.no/api/v1/docs) (Du må be om tilgang til onprem-k8s-prod i
  naisdevice)
- [Dev-fss](https://sokos-skattekort-person.intern.dev.nav.no/api/v1/docs)
- [Lokalt](http://0.0.0.0:8080/api/v1/docs)

# 8. Henvendelser og tilgang

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på Github.\
Interne henvendelser kan sendes via Slack i kanalen `#po-utbetaling`\
Tilgang til denne tjenesten kan bestilles
gjennom [Porten](https://jira.adeo.no/plugins/servlet/desk/portal/541?requestGroup=824)
