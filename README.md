# sokos-skattekort-person

# Innholdsoversikt
* [1. Funksjonelle krav](#1-funksjonelle-krav)
* [2. Utviklingsmiljø](#2-utviklingsmiljø)
* [3. Programvarearkitektur](#3-programvarearkitektur)
* [4. Deployment](#4-deployment)
* [5. Autentisering](#5-autentisering)
* [6. Drift og støtte](#6-drift-og-støtte)
* [7. Swagger](#7-swagger)
* [8. Henvendelser](#7-henvendelser)
---

# 1. Funksjonelle Krav
Hva er oppgaven til denne applikasjonen

# 2. Utviklingsmiljø
### Forutsetninger
* Java 17

### Bygge prosjekt
Hvordan bygger jeg prosjektet.

### Lokal utvikling
Hvordan kan jeg kjøre lokalt og hva trenger jeg?

# 3. Programvarearkitektur
Legg ved skissediagram for hvordan arkitekturen er bygget

# 4. Deployment
Distribusjon av tjenesten er gjort med bruk av Github Actions.
[sokos-skattekort-person CI / CD](https://github.com/navikt/sokos-skattekort-person/actions)

Push/merge til master branche vil teste, bygge og deploye til produksjonsmiljø og testmiljø.
Det foreligger også mulighet for manuell deploy.

# 7. Autentisering
Applikasjonen bruker [AzureAD](https://docs.nais.io/security/auth/azure-ad/) autentisering

### Hente token
1. Installer `vault` kommandolinje verktøy: https://github.com/navikt/utvikling/blob/main/docs/teknisk/Vault.md
2. For å vise token skal vi installere jq:
   - `brew install jq` (på mac)
   - `choco install jq` for windows (install chocolatey: https://community.chocolatey.org/packages/autocomplete)
   - `apt-get install jq` (på ubuntu).
3. Gi rettighet for å kjøre scriptet `chmod 755 getToken.sh`
4. Kjør scriptet:
   ```
   ./getToken.sh
   ```
4. Skriv inn applikasjonsnavn du vil hente `client_id` og `client_secret` for

# 6. Drift og støtte

### Logging
Hvor finner jeg logger? Hvordan filtrerer jeg mellom dev og prod logger?

[sikker-utvikling/logging](https://sikkerhet.nav.no/docs/sikker-utvikling/logging) - Anbefales å lese

### Kubectl
For dev-fss:
```shell script
kubectl config use-context dev-gcp
kubectl get pods -n okonomi | grep sokos-skattekort-person
kubectl logs -f sokos-skattekort-person-<POD-ID> --namespace okonomi -c sokos-skattekort-person
```

For prod-gcp:
```shell script
kubectl config use-context prod-gcp
kubectl get pods -n okonomi | grep sokos-skattekort-person
kubectl logs -f sokos-skattekort-person-<POD-ID> --namespace okonomi -c sokos-skattekort-person
```

### Alarmer
Vi bruker [nais-alerts](https://doc.nais.io/observability/alerts) for å sette opp alarmer. Disse finner man konfigurert i [.nais/alerterator.yaml](.nais/alerterator.yaml) filen.

### Grafana
- [appavn](url)
---

# 7. Swagger
Hva er url til Lokal, dev og prod?

