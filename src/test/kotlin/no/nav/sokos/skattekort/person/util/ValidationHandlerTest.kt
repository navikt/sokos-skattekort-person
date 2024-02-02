package no.nav.sokos.skattekort.person.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Year

internal class ValidationHandlerTest : FunSpec({

    test("fnr er gyldig") {
        val result = validFnr("12345678901")
        result shouldBe true
    }

    test("fnr er ugyldig, mindre enn 11 siffer") {
        val result = validFnr("1234567890")
        result shouldBe false
    }

    test("fnr er ugyldig, mer enn 11 siffer") {
        val result = validFnr("123456789012")
        result shouldBe false
    }

    test("fnr er ugyldig med bokstaver") {
        val result = validFnr("1234567890a")
        result shouldBe false
    }

    test("årstall er ugyldig med bokstaver nåværende år") {
        val result = validYear("asdasdasd")
        result shouldBe false
    }

    test("årstall er gyldig, nåværende år minus 1") {
        val result = validYear(Year.now().minusYears(1).toString())
        result shouldBe true
    }

    test("årstall er gyldig, nåværende år pluss 1") {
        val result = validYear(Year.now().plusYears(1).toString())
        result shouldBe true
    }

    test("årstall er ugyldig, mindre enn nåværende år minus 1") {
        val result = validYear(Year.now().minusYears(2).toString())
        result shouldBe false
    }

    test("årstall er ugyldig, større enn nåværende år pluss 1") {
        val result = validYear(Year.now().plusYears(2).toString())
        result shouldBe false
    }

})