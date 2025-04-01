package no.nav.sokos.skattekort.person.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.zaxxer.hikari.HikariDataSource
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using

import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.config.DatabaseConfig
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

private val xmlMapper: ObjectMapper =
    XmlMapper(JacksonXmlModule().apply { setDefaultUseWrapper(false) })
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
