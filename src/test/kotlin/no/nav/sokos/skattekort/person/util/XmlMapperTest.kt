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
        val frikort = "xml/frikort.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(frikort, SkattekortTilArbeidsgiver::class.java)

        val forskuddstrekk =
            skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort?.forskuddstrekk

        forskuddstrekk!!.first().shouldBeInstanceOf<Frikort>()
        forskuddstrekk.size.shouldBe(3)


    }

    test("test konvertering av trekkprosent xml til SkattekortTilArbeidsgiver klasse") {
        val trekkprosent = "xml/trekkprosent.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekkprosent, SkattekortTilArbeidsgiver::class.java)

        val forskuddstrekk =
            skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort?.forskuddstrekk

        forskuddstrekk!!.first().shouldBeInstanceOf<Trekkprosent>()
        forskuddstrekk.size.shouldBe(1)

        skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().tilleggsopplysning!!.first().value
            .shouldContain("kildeskattPaaLoenn")

    }

    test("test konvertering av trekktabell xml til SkattekortTilArbeidsgiver klasse") {
        val trekktabell = "xml/trekktabell.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekktabell, SkattekortTilArbeidsgiver::class.java)

        val forskuddstrekk =
            skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort?.forskuddstrekk

        forskuddstrekk!!.first().shouldBeInstanceOf<Trekktabell>()
        forskuddstrekk.size.shouldBe(6)

    }

    test("test konvertering av ikkeSkattekort xml til SkattekortTilArbeidsgiver klasse") {
        val ikkeSkattekort = "xml/ikkeSkattekort.xml".readFromResource()

        val skattekortTilArbeidsgiver = xmlMapper.readValue(ikkeSkattekort, SkattekortTilArbeidsgiver::class.java)

        val ingenSkattekort =
            skattekortTilArbeidsgiver.arbeidsgiver.first().arbeidstaker.first().skattekort

        ingenSkattekort shouldBe null
    }

})