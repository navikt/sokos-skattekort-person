package no.nav.sokos.skattekort.person.auditlogg

import no.nav.sokos.skattekort.person.config.auditlogger

class AuditLogger {
    fun auditLog(auditLoggData: AuditLogg) {
        auditlogger.info(auditLoggData.logMessage())
    }
}