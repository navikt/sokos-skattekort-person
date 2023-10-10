package no.nav.sokos.skattekort.person.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Year

internal class ValidationHandlerTest : FunSpec({

    test("test når input string består av tall") {
        val result = validInput("123")
        result shouldBe true
    }

    test("test når input string består av bokstaver") {
        val result = validInput("abc")
        result shouldBe false
    }

    test("test når input string består av tegn") {
        val result = validInput("%(&%/&")
        result shouldBe false
    }

    test("test årstall er gyldig nåværende år minus 1") {
        val year = Year.now().value - 1
        val result = validYear(year.toString())
        result shouldBe false
    }

    test("test årstall er gyldig nåværende år pluss 1") {
        val year = Year.now().value + 1
        val result = validYear(year.toString())
        result shouldBe false
    }

    test("test årstall ikke er mindre enn nåværende år minus 1") {
        val year = Year.now().value - 2
        val result = validYear(year.toString())
        result shouldBe true
    }

    test("test årstall ikke er mer enn nåværende år pluss 1") {
        val year = Year.now().value + 2
        val result = validYear(year.toString())
        result shouldBe true
    }

})