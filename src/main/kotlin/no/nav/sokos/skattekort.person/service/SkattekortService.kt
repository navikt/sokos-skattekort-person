package no.nav.sokos.skattekort.person.service

import no.nav.sokos.skattekort.person.domain.Skattekort

class SkattekortService() {

    fun hentSkattekort(): Skattekort {
        return Skattekort("Ola", "Nordmann", "2023")
    }
}