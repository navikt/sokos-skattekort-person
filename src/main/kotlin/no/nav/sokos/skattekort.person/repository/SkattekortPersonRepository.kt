package no.nav.sokos.skattekort.person.repository

import com.zaxxer.hikari.HikariDataSource
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using

import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.config.DatabaseConfig
import no.nav.sokos.skattekort.person.config.xmlMapper
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver

class SkattekortPersonRepository(
    private val dataSource: HikariDataSource = DatabaseConfig.oracleDataSource,
) {
    fun hentSkattekortPaaFnrOgInntektsAar(skattekortPersonRequest: SkattekortPersonRequest): List<SkattekortTilArbeidsgiver> =
        using(sessionOf(dataSource)) { session ->
            session.list(
                queryOf(
                    """
                    SELECT NVL2(DATA_MOTTATT, (DATA_MOTTATT).getClobVal(), null) 
                    FROM T1_SKATTEKORT_BESTILLING 
                    WHERE FNR = ? AND INNTEKTSAAR = ?
                    """.trimIndent(),
                    skattekortPersonRequest.fnr,
                    skattekortPersonRequest.inntektsaar,
                ),
            ) { row -> xmlMapper.readValue(row.string(1), SkattekortTilArbeidsgiver::class.java) }
        }
}
