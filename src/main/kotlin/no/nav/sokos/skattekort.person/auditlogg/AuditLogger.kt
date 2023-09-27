package no.nav.sokos.skattekort.person.auditlogg

import mu.KotlinLogging
import no.nav.sokos.skattekort.person.config.AUDIT_LOGGER

private val auditlogger = KotlinLogging.logger(AUDIT_LOGGER)

class AuditLogger {
    fun auditLog(auditLoggData: AuditLogg) {
        auditlogger.info(auditLoggData.logMessage())
    }
}