package no.nav.sokos.skattekort.person.database

import java.sql.Connection
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.database.RepositoryExtensions.param
import no.nav.sokos.skattekort.person.database.RepositoryExtensions.toList
import no.nav.sokos.skattekort.person.database.RepositoryExtensions.withParameters
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.util.xmlMapper

object SkattekortPersonRepository {
    fun Connection.hentSkattekortPaaFnrOgInntektsAar(
        skattekortPersonRequest: SkattekortPersonRequest
    ): List<SkattekortTilArbeidsgiver> =
        prepareStatement(
            """
            SELECT NVL2(DATA_MOTTATT, (DATA_MOTTATT).getClobVal(), null)
            FROM OSESKATT_U4.T1_SKATTEKORT_BESTILLING
            WHERE FNR = (?) AND INNTEKTSAAR = (?)
        """
        ).withParameters(
            param(skattekortPersonRequest.fnr),
            param(skattekortPersonRequest.inntektsaar)
        ).run {
            executeQuery().toList {
                val xmlSkattekort = getString(1)
                xmlMapper.readValue(xmlSkattekort, SkattekortTilArbeidsgiver::class.java)
            }
        }
}