package no.nav.sokos.skattekort.person.service

import no.nav.sokos.skattekort.person.api.model.SkattekortPerson
import no.nav.sokos.skattekort.person.config.xmlMapper
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.database.hentSkattekortPaaFnrOgInntektsAar
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver

class SkattekortPersonService(
    private val oracleDataSource: OracleDataSource
) {

    fun hentSkattekortPerson(
        skattekortPerson: SkattekortPerson
    ): SkattekortTilArbeidsgiver {
        val skattekortXml = oracleDataSource.connection.use {
            with(skattekortPerson) {
                it.hentSkattekortPaaFnrOgInntektsAar(skattekortPerson)
            }
        }
        println("Åssen ser denne ut= $skattekortXml")
        return xmlMapper.readValue(skattekortXml, SkattekortTilArbeidsgiver::class.java)
    }
}