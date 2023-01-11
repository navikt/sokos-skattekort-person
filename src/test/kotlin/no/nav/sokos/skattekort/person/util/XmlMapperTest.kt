package no.nav.sokos.skattekort.person.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.sokos.skattekort.person.domain.Frikort
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.domain.Trekkprosent
import no.nav.sokos.skattekort.person.domain.Trekktabell
import no.nav.sokos.skattekort.person.readFromResource


class XmlMapperTest : FunSpec({

    test("test konverting av frikort xml til SkattekortTilArbeidsgiver klasse") {
        val frikortXml = "frikort.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(frikortXml, SkattekortTilArbeidsgiver::class.java)

        val forskuddstrekk =
            skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort.forskuddstrekk

        forskuddstrekk!!.first().shouldBeInstanceOf<Frikort>()
        forskuddstrekk.size.shouldBe(3)


    }

    test("test konvertering av trekkprosent xml til SkattekortTilArbeidsgiver klasse") {
        val trekkprosent = "trekkprosent.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekkprosent, SkattekortTilArbeidsgiver::class.java)

        val forskuddstrekk =
            skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort.forskuddstrekk

        forskuddstrekk!!.first().shouldBeInstanceOf<Trekkprosent>()
        forskuddstrekk.size.shouldBe(1)

        skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().tilleggsopplysning!!.first().value
            .shouldContain("kildeskattPaaLoenn")

    }

    test("test konvertering av trekktabell xml til SkattekortTilArbeidsgiver klasse") {
        val trekktabell = "trekktabell.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekktabell, SkattekortTilArbeidsgiver::class.java)

        val forskuddstrekk =
            skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort.forskuddstrekk

        forskuddstrekk!!.first().shouldBeInstanceOf<Trekktabell>()
        forskuddstrekk.size.shouldBe(6)

    }

})