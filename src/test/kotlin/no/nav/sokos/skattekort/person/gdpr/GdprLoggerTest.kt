package no.nav.sokos.skattekort.person.gdpr

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith

internal class GdprLoggerTest: FunSpec({

    test("test auditLog") {
        val expectedLogMessageStart = "CEF:0|OKONOMI|AuditLogger|1.0|audit:access|sokos-skattekort-person|INFO|suid=Z12345 duid=24417337179 end="
        val expectedLogMessageEnd = " msg=NAV-ansatt har søkt etter skattekort for bruker"
        val logData = GdprLogg(
            saksbehandler = "Z12345",
            fnr = "24417337179"
        )

        logData.logMessage() shouldStartWith expectedLogMessageStart
        logData.logMessage() shouldEndWith expectedLogMessageEnd
    }
})