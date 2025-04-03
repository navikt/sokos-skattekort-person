
---
sokos-skattekort-person
---

```mermaid
flowchart LR
    ssp("sokos-skattekort-person")
    up("Utbetalingsportalen")
    nks("Nav Kontaktsenter")
    oracle[(os-eskatt Oracle DB)]
    up --> |REST| ssp
    nks --> |REST| ssp
    ssp --> |READ| oracle
```