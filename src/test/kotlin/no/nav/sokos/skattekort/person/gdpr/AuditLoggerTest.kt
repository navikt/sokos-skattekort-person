package no.nav.sokos.skattekort.person.gdpr

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith

import no.nav.sokos.skattekort.person.auditlogg.AuditLogg

internal class AuditLoggerTest : FunSpec({

    test("test auditLogger har riktig melding format") {
        val expectedLogMessageStart =
            "CEF:0|Utbetalingsportalen|sokos-skattekort-person|1.0|audit:access|sokos-skattekort-person|INFO|suid=Z12345 duid=24417337179 end="
        val expectedLogMessageEnd = " msg=NAV-ansatt har søkt etter skattekort for bruker"
        val logData =
            AuditLogg(
                saksbehandler = "Z12345",
                fnr = "24417337179",
            )

        logData.logMessage() shouldStartWith expectedLogMessageStart
        logData.logMessage() shouldEndWith expectedLogMessageEnd
    }
})
