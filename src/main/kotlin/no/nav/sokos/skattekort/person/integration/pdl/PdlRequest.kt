package no.nav.sokos.skattekort.person.integration.pdl
import kotlinx.serialization.Serializable

@Serializable
data class PdlRequest(
    val ident: String
)
