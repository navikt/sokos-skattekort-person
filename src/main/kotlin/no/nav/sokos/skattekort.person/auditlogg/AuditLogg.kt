package no.nav.sokos.skattekort.person.auditlogg

/**
 * Loggeformat
 * https://sikkerhet.nav.no/docs/sikker-utvikling/auditlogging#beskrivelse-av-cef
 */

data class AuditLogg(
    val saksbehandler: String,
    val fnr: String,
) {
    private val version = "0"
    private val deviceVendor = "Utbetalingsportalen"
    private val deviceProduct = "sokos-skattekort-person"
    private val deviceVersion = "1.0"
    private val deviceEventClassId = "audit:access"
    private val name = "sokos-skattekort-person"
    private val severity = "INFO"
    private val brukerhandling = "NAV-ansatt har søkt etter skattekort for bruker"

    fun logMessage(): String {
        val extension = "suid=$saksbehandler duid=$fnr end=${System.currentTimeMillis()} msg=$brukerhandling"

        return "CEF:$version|$deviceVendor|$deviceProduct|$deviceVersion|$deviceEventClassId|$name|$severity|$extension"
    }
}
