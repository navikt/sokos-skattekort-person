package no.nav.sokos.skattekort.person.api.model

import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver

data class SkattekortPersonResponse(
    val data: List<SkattekortTilArbeidsgiver>
)