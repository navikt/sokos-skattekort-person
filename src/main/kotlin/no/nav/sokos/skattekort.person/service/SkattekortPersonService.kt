package no.nav.sokos.skattekort.person.service

import io.ktor.server.application.ApplicationCall
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.auditlogg.AuditLogg
import no.nav.sokos.skattekort.person.auditlogg.AuditLogger
import no.nav.sokos.skattekort.person.auditlogg.Saksbehandler
import no.nav.sokos.skattekort.person.config.logger
import no.nav.sokos.skattekort.person.config.secureLogger
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.skattekort.person.database.SkattekortPersonRepository.hentSkattekortPaaFnrOgInntektsAar
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.pdl.PdlService
import no.nav.sokos.skattekort.person.security.getSaksbehandler

class SkattekortPersonService(
    private val oracleDataSource: OracleDataSource = OracleDataSource(),
    private val auditLogger: AuditLogger = AuditLogger(),
    private val pdlService: PdlService = PdlService(),
) {

    fun hentSkattekortPerson(
        skattekortPersonRequest: SkattekortPersonRequest,
        applicationCall: ApplicationCall,
    ): List<SkattekortTilArbeidsgiver> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter skattekort")
        secureLogger.info("Henter skattekort for person: ${skattekortPersonRequest.toJson()}")
        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident, fnr = skattekortPersonRequest.fnr))

        val navn = hentNavnFraPdl(skattekortPersonRequest.fnr)
        val skattekort = oracleDataSource.connection.useAndHandleErrors { connection ->
            connection.hentSkattekortPaaFnrOgInntektsAar(skattekortPersonRequest)
        }

        if (navn.isBlank() && skattekort.isEmpty()) {
            logger.info("Fant ikke skattekort for person")
            secureLogger.info("Fant ikke skattekort for person: ${skattekortPersonRequest.toJson()}")
            return emptyList()
        }

        return listOf(
            SkattekortTilArbeidsgiver(
                navn = navn,
                arbeidsgiver = skattekort.firstOrNull()?.arbeidsgiver ?: emptyList()
            )
        )
    }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }

    private fun hentNavnFraPdl(ident: String): String {
        return pdlService.getPersonNavn(ident)?.navn?.firstOrNull()
            ?.run { mellomnavn?.let { "$fornavn $mellomnavn $etternavn" } ?: "$fornavn $etternavn" } ?: ""
    }
}