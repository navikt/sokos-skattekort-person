package no.nav.sokos.skattekort.person

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.sokos.skattekort.person.util.Util.readFromResource
import no.nav.sokos.skattekort.person.config.xmlMapper
import no.nav.sokos.skattekort.person.model.Frikort
import no.nav.sokos.skattekort.person.model.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.model.Trekkprosent
import no.nav.sokos.skattekort.person.model.Trekktabell


class XmlToSkattekortTilArbeidsgiverTest : FunSpec({

    test("frikort xml til SkattekortTilArbeidsgiver klasse med riktig frikort forskuddstrekk") {
        val frikortXml = "frikort.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(frikortXml, SkattekortTilArbeidsgiver::class.java)

        skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort.forskuddstrekk!!.first()
            .shouldBeInstanceOf<Frikort>()

    }

    test("trekkprosent xml til SkattekortTilArbeidsgiver klasse med riktig trekkprosent forskuddstrekk") {
        val trekkprosent = "trekkprosent.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekkprosent, SkattekortTilArbeidsgiver::class.java)

        skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort.forskuddstrekk!!.first()
            .shouldBeInstanceOf<Trekkprosent>()

        skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().tilleggsopplysning!!.first().value
            .shouldContain("kildeskattPaaLoenn")

    }

    test("trekktabell xml til SkattekortTilArbeidsgiver klasse med riktig trekktabell forskuddstrekk") {
        val trekktabell = "trekktabell.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekktabell, SkattekortTilArbeidsgiver::class.java)

        skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort.forskuddstrekk!!.first()
            .shouldBeInstanceOf<Trekktabell>()

    }
})