package no.nav.sokos.skattekort.person

import no.nav.sokos.skattekort.person.util.Util.readFromResource
import no.nav.sokos.skattekort.person.config.xmlMapper
import no.nav.sokos.skattekort.person.model.SkattekortTilArbeidsgiver
import org.junit.jupiter.api.Test


class XmlToSkattekortTilArbeidsgiverTest {

    @Test
    fun `tester konverting fra frikort xml til SkattekortTilArbeidsgiver klasse`() {
        val frikortXml = "frikort.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(frikortXml, SkattekortTilArbeidsgiver::class.java)
    }

    @Test
    fun `tester konverting fra trekkprosent xml til SkattekortTilArbeidsgiver klasse`() {
        val trekkprosentXml = "trekkprosent.xml".readFromResource();

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekkprosentXml, SkattekortTilArbeidsgiver::class.java)
    }

    @Test
    fun `tester konverting fra trekktabell xml til SkattekortTilArbeidsgiver klasse`() {
        val trekktabellXml = "trekktabell.xml".readFromResource();

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekktabellXml, SkattekortTilArbeidsgiver::class.java)
    }
}