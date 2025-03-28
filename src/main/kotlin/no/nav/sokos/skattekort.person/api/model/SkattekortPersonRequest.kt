package no.nav.sokos.skattekort.person.api.model

import no.nav.sokos.skattekort.person.config.jsonMapper

data class SkattekortPersonRequest(
    val fnr: String,
    val inntektsaar: String,
) {
    fun toJson(): String {
        return jsonMapper.writeValueAsString(this)
    }
}
