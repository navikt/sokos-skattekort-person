package no.nav.sokos.skattekort.person.service

import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.skattekort.person.database.SkattekortPersonRepository.hentSkattekortPaaFnrOgInntektsAar
import no.nav.sokos.skattekort.person.gdpr.Saksbehandler
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.gdpr.GdprLogg
import no.nav.sokos.skattekort.person.gdpr.GdprLogger

class SkattekortPersonService(
    private val oracleDataSource: OracleDataSource,
    private val gdrpLogger: GdprLogger
) {

    fun hentSkattekortPerson(
        skattekortPersonRequest: SkattekortPersonRequest,
        saksbehandler: Saksbehandler
    ): List<SkattekortTilArbeidsgiver> {
        gdrpLogger.auditLog(GdprLogg(saksbehandler = saksbehandler.ident, fnr = skattekortPersonRequest.fnr))
        oracleDataSource.connection.useAndHandleErrors { connection ->
            return connection.hentSkattekortPaaFnrOgInntektsAar(skattekortPersonRequest)
        }
    }
}