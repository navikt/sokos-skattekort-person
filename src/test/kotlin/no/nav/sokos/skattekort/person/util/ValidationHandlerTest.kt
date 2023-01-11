package no.nav.sokos.skattekort.person.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Year

internal class ValidationHandlerTest: FunSpec ({

    test("test når input string består av tall") {
        val result = isNumberInputValidNumber("123")
        result shouldBe true
    }

    test("test når input string består av bokstaver") {
        val result = isNumberInputValidNumber("abc")
        result shouldBe false
    }

    test("test når input string består av tegn") {
        val result = isNumberInputValidNumber("%(&%/&")
        result shouldBe false
    }

    test("test årstall er gyldig nåværende år") {
        val result = isYearInputMoreThanCurrentYear(Year.now().value)
        result shouldBe false
    }

    test("test årstall ikke er mer enn nåværende år") {
        val result = isYearInputMoreThanCurrentYear(Year.now().value + 1)
        result shouldBe true
    }

    test("test årstall er gyldig nåværende år minus 1") {
        val result = isYearInputLessThanPreviousYear(Year.now().value - 1)
        result shouldBe false
    }

    test("test årstall ikke er mindre nn nåværende år minus 1") {
        val result = isYearInputLessThanPreviousYear(Year.now().value - 2)
        result shouldBe true
    }
})