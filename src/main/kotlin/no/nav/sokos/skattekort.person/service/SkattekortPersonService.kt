package no.nav.sokos.skattekort.person.service

import io.ktor.server.application.ApplicationCall
import mu.KotlinLogging
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.auditlogg.AuditLogg
import no.nav.sokos.skattekort.person.auditlogg.AuditLogger
import no.nav.sokos.skattekort.person.auditlogg.Saksbehandler
import no.nav.sokos.skattekort.person.config.SECURE_LOGGER
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.skattekort.person.database.SkattekortPersonRepository.hentSkattekortPaaFnrOgInntektsAar
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.pdl.PdlService
import no.nav.sokos.skattekort.person.security.getSaksbehandler

private val logger = KotlinLogging.logger {}
private val secureLogger = KotlinLogging.logger(SECURE_LOGGER)

class SkattekortPersonService(
    private val oracleDataSource: OracleDataSource,
    private val auditLogger: AuditLogger,
    private val pdlService: PdlService = PdlService(),
) {

    fun hentSkattekortPerson(
        skattekortPersonRequest: SkattekortPersonRequest,
        applicationCall: ApplicationCall,
    ): List<SkattekortTilArbeidsgiver> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter skattekort")
        secureLogger.info("Henter skattekort for person: ${skattekortPersonRequest.toJson()}")

        val person = hentNavnFraPdl(skattekortPersonRequest.fnr)
        println("HVA KOMMER UT HER? $person")

        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident, fnr = skattekortPersonRequest.fnr))
        oracleDataSource.connection.useAndHandleErrors { connection ->
            return connection.hentSkattekortPaaFnrOgInntektsAar(skattekortPersonRequest)
        }
    }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }

    private fun hentNavnFraPdl(ident: String): String {
        return pdlService.getPersonNavn(ident)?.navn?.firstOrNull()
            ?.run { mellomnavn?.let { "$fornavn $mellomnavn $etternavn" } ?: "$fornavn $etternavn" } ?: ""
    }
}