package no.nav.sokos.skattekort.person.gdpr

import mu.KotlinLogging
import no.nav.sokos.skattekort.person.config.AUDIT_LOGGER

private val auditlogger = KotlinLogging.logger(AUDIT_LOGGER)

class GdprLogger {
    fun auditLog(logData: GdprLogg) {
        auditlogger.info(logData.logMessage())
    }
}