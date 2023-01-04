package no.nav.sokos.skattekort.person.api.model

data class SkattekortPersonRequest(
    val fnr: String,
    val inntektsaar: String
)