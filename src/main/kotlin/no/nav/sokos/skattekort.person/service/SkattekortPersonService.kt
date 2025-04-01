package no.nav.sokos.skattekort.person.service

import io.ktor.server.application.ApplicationCall
import mu.KotlinLogging

import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.auditlogg.AuditLogg
import no.nav.sokos.skattekort.person.auditlogg.AuditLogger
import no.nav.sokos.skattekort.person.auditlogg.Saksbehandler
import no.nav.sokos.skattekort.person.config.SECURE_LOGGER
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.pdl.PdlService
import no.nav.sokos.skattekort.person.repository.SkattekortPersonRepository
import no.nav.sokos.skattekort.person.security.getSaksbehandler

private val logger = KotlinLogging.logger {}
private val secureLogger = KotlinLogging.logger(SECURE_LOGGER)

class SkattekortPersonService(
    private val skattekortPersonRepository: SkattekortPersonRepository = SkattekortPersonRepository(),
    private val auditLogger: AuditLogger = AuditLogger(),
    private val pdlService: PdlService = PdlService(),
) {
    fun hentSkattekortPerson(
        skattekortPersonRequest: SkattekortPersonRequest,
        applicationCall: ApplicationCall,
    ): List<SkattekortTilArbeidsgiver> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter skattekort")
        secureLogger.info("Henter skattekort for person: $skattekortPersonRequest")
        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident, fnr = skattekortPersonRequest.fnr))

        val navn = hentNavnFraPdl(skattekortPersonRequest.fnr)
        val skattekort = skattekortPersonRepository.hentSkattekortPaaFnrOgInntektsAar(skattekortPersonRequest)

        if (navn.isBlank() && skattekort.isEmpty()) {
            logger.info("Fant ikke skattekort for person")
            secureLogger.info("Fant ikke skattekort for person: $skattekortPersonRequest")
            return emptyList()
        }

        return listOf(
            SkattekortTilArbeidsgiver(
                navn = navn,
                arbeidsgiver = skattekort.firstOrNull()?.arbeidsgiver ?: emptyList(),
            ),
        )
    }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler = getSaksbehandler(call)

    private fun hentNavnFraPdl(ident: String): String =
        pdlService
            .getPersonNavn(ident)
            ?.navn
            ?.firstOrNull()
            ?.run { mellomnavn?.let { "$fornavn $mellomnavn $etternavn" } ?: "$fornavn $etternavn" } ?: ""
}
