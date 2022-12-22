package no.nav.sokos.skattekort.person.service

import no.nav.sokos.skattekort.person.api.model.SkattekortRequest
import no.nav.sokos.skattekort.person.config.xmlMapper
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.database.hentSkattekortPaFnr
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver

class SkattekortService(
    private val oracleDataSource: OracleDataSource
) {

    fun hentSkattekortPaFnr(
        skattekortRequest: SkattekortRequest
    ): SkattekortTilArbeidsgiver {
        val skattekortXml = oracleDataSource.connection.use {
            with(skattekortRequest) {
                it.hentSkattekortPaFnr(skattekortRequest)
            }
        }
        return xmlMapper.readValue(skattekortXml, SkattekortTilArbeidsgiver::class.java)
    }
}