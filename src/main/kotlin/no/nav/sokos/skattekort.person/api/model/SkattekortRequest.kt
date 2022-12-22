package no.nav.sokos.skattekort.person.api.model

data class SkattekortRequest(
    val fnr: String,
    val inntektsaar: String
)