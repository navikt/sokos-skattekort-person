package no.nav.sokos.skattekort.person.auditlogg

/**
 * Loggeformat
 * https://sikkerhet.nav.no/docs/sikker-utvikling/auditlogging#beskrivelse-av-cef
 */


data class AuditLogg(
    val saksbehandler: String,
    val fnr: String,
) {
    val version = "0"
    val deviceVendor = "OKONOMI"
    val deviceProduct = "AuditLogger"
    val deviceVersion = "1.0"
    val deviceEventClassId = "audit:access"
    val name = "sokos-skattekort-person"
    val severity = "INFO"
    val brukerhandling = "NAV-ansatt har søkt etter skattekort for bruker"

    fun logMessage(): String {
        val extension = "suid=$saksbehandler duid=$fnr end=${System.currentTimeMillis()} msg=$brukerhandling"

        return "CEF:$version|$deviceVendor|$deviceProduct|$deviceVersion|$deviceEventClassId|$name|$severity|$extension"
    }
}