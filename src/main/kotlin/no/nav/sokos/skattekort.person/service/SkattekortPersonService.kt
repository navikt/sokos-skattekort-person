package no.nav.sokos.skattekort.person.service

import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.skattekort.person.database.SkattekortPersonRepository.hentSkattekortPaaFnrOgInntektsAar
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver

class SkattekortPersonService(
    private val oracleDataSource: OracleDataSource
) {

    fun hentSkattekortPerson(
        skattekortPersonRequest: SkattekortPersonRequest
    ): List<SkattekortTilArbeidsgiver> {
        oracleDataSource.connection.useAndHandleErrors { connection ->
            return connection.hentSkattekortPaaFnrOgInntektsAar(skattekortPersonRequest)
        }
    }
}