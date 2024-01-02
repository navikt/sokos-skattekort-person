package no.nav.sokos.skattekort.person.util

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import java.time.Year
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest

fun RequestValidationConfig.validationHandler() {
    validate<SkattekortPersonRequest> { skattekortPersonRequest ->

        when {
            !validInput(skattekortPersonRequest.inntektsaar) -> ValidationResult.Invalid("Inntektsår er ugyldig")

            validYear(skattekortPersonRequest.inntektsaar) ->
                ValidationResult.Invalid("Inntektsår kan ikke være utenfor intervallet ${Year.now().minusYears(1)} til ${Year.now().plusYears(1)}")

            !validInput(skattekortPersonRequest.fnr) -> ValidationResult.Invalid("Fødelsnummer er ugyldig")
            !validFnr(skattekortPersonRequest.fnr) -> ValidationResult.Invalid("Fødelsnummer må være 11 siffer")
            else -> ValidationResult.Valid
        }
    }
}

fun validInput(numberInput: String): Boolean {
    return numberInput.isEmpty() || numberInput.all { it.isDigit() }
}

fun validYear(year: String): Boolean {
    val currentYear = Year.now().value
    return year.toInt() < currentYear.minus(1) || year.toInt() > currentYear.plus(1)
}

fun validFnr(fnr: String): Boolean {
    return fnr.length == 11
}