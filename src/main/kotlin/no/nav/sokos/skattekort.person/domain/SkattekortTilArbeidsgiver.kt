package no.nav.sokos.skattekort.person.domain

data class SkattekortTilArbeidsgiver(
    val person: String? = null,
    val arbeidsgiver: List<Arbeidsgiver>
)