package no.nav.sokos.skattekort.person.api.model

import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.util.jsonMapper

data class SkattekortPersonResponse(
    val skattekortListe: List<SkattekortTilArbeidsgiver>
)