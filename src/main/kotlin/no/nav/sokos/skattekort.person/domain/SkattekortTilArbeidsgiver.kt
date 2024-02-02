package no.nav.sokos.skattekort.person.domain

data class SkattekortTilArbeidsgiver(
    var navn: String? = null,
    val arbeidsgiver: List<Arbeidsgiver>
)