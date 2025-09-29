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
* [Gradle](https://gradle.org/)
* [Kotest IntelliJ Plugin](https://plugins.jetbrains.com/plugin/14080-kotest)

### Bygge prosjekt

`./gradlew build installDist`

### Lokal utvikling

For å kjøre applikasjonen må du gjøre følgende:

- Kjør scriptet [setupLocalEnvironment.sh](setupLocalEnvironment.sh)
     ```
     chmod 755 setupLocalEnvironment.sh && ./setupLocalEnvironment.sh
     ```
  Denne vil opprette [defaults.properties](defaults.properties) med alle environment variabler du trenger for å kjøre
  applikasjonen som er definert
  i [PropertiesConfig](src/main/kotlin/no/nav/sokos/skattekort.person/config/PropertiesConfig.kt).
  Her vil du også kunne f.eks endre om du ønsker slå på autentisering eller ikke i
  koden `"USE_AUTHENTICATION" to "true"` i
  filen [PropertiesConfig](src/main/kotlin/no/nav/sokos/skattekort.person/config/PropertiesConfig.kt).
  Husk å endre
- `DATABASE_HOST=10.53.103.79` (nåes gjennom naisdevice)
- `DATABASE_NAME=oseskatt_u4`
- `DATABASE_SCHEMA=oseskatt_u4`
- `oseskatt_u4` databasen fordi dette er eneste databasen som kan nåes
  via [naisdevice](https://docs.nais.io/device/?h=naisdevice)

# 3. Programvarearkitektur

[System diagram](./dokumentasjon/system-diagram.md)

# 4. Deployment

Distribusjon av tjenesten er gjort med bruk av Github Actions.
[sokos-skattekort-person CI / CD](https://github.com/navikt/sokos-skattekort-person/actions)

Push/merge til main branch direkte er ikke mulig. Det må opprettes PR og godkjennes før merge til main branch.
Når PR er merged til main branch vil Github Actions bygge og deploye til dev-fss og prod-fss.
Har også mulighet for å deploye manuelt til testmiljø ved å deploye PR.

# 5. Autentisering

Applikasjonen bruker [AzureAD](https://docs.nais.io/security/auth/azure-ad/) autentisering

# 6. Drift og støtte

### Logging

Feilmeldinger og infomeldinger som ikke innheholder sensitive data logges til [Grafana Loki](https://docs.nais.io/observability/logging/#grafana-loki).  
Sensitive meldinger logges til [Team Logs](https://doc.nais.io/observability/logging/how-to/team-logs/).

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

Applikasjonen bruker [Grafana Alerting](https://grafana.nav.cloud.nais.io/alerting/) for overvåkning og varsling.

Alarmene overvåker metrics som:

- HTTP-feilrater
- JVM-metrikker

Varsler blir sendt til følgende Slack-kanaler:

- Dev-miljø: [#team-mob-alerts-dev](https://nav-it.slack.com/archives/C042SF2FEQM)
- Prod-miljø: [#team-mob-alerts-prod](https://nav-it.slack.com/archives/C042ESY71GX)

### Grafana

- [sokos-skattekort-person dashboard](https://grafana.nais.io/d/JC31xmTVz/sokos-skattekort-person?orgId=1&refresh=30s&from=now-24h&to=now)

---

# 7. Swagger

- [Prod-fss](https://sokos-skattekort-person.intern.nav.no/api/v1/docs) (Du må be om tilgang til onprem-k8s-prod i
  naisdevice)
- [Dev-fss](https://sokos-skattekort-person.intern.dev.nav.no/api/v1/docs)
- [Lokalt](http://0.0.0.0:8080/api/v1/docs)

# 8. Henvendelser og tilgang

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på Github.
Interne henvendelser kan sendes via Slack i kanalen [#utbetaling](https://nav-it.slack.com/archives/CKZADNFBP)
Tilgang til denne tjenesten kan bestilles
gjennom [Porten](https://jira.adeo.no/plugins/servlet/desk/portal/541?requestGroup=824)
