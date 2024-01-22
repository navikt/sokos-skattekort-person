package no.nav.sokos.skattekort.person.domain

data class SkattekortTilArbeidsgiver(
    val navn: String? = null,
    val arbeidsgiver: List<Arbeidsgiver>
)