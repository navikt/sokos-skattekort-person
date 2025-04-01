package no.nav.sokos.skattekort.person.api.model

import kotlinx.serialization.Serializable

@Serializable
data class SkattekortPersonRequest(
    val fnr: String,
    val inntektsaar: String,
)
