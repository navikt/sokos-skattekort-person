package no.nav.sokos.skattekort.person.service

import no.nav.sokos.skattekort.person.api.model.SkattekortPerson
import no.nav.sokos.skattekort.person.config.xmlMapper
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.database.hentSkattekortPaaFnrOgInntektsaar
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver

class SkattekortPersonService(
    private val oracleDataSource: OracleDataSource
) {

    fun hentSkattekortPerson(
        skattekortPerson: SkattekortPerson
    ): SkattekortTilArbeidsgiver {
        val skattekortXml = oracleDataSource.connection.use {
            with(skattekortPerson) {
                it.hentSkattekortPaaFnrOgInntektsaar(skattekortPerson)
            }
        }
        return xmlMapper.readValue(skattekortXml, SkattekortTilArbeidsgiver::class.java)
    }
}