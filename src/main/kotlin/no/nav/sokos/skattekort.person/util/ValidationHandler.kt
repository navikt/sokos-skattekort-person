package no.nav.sokos.skattekort.person.util

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import java.time.Year
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest

fun RequestValidationConfig.validationHandler() {

    validate<SkattekortPersonRequest> { skattekortPersonRequest ->

        when {
            !isNumberInputValidNumber(skattekortPersonRequest.inntektsaar) ->
                ValidationResult.Invalid("Inntektår er ugyldig")

            isYearInputLessThanPreviousYear(skattekortPersonRequest.inntektsaar.toInt()) ->
                ValidationResult.Invalid("Inntektsår kan ikke være mindre enn ${Year.now().value - 1}")

            isYearInputMoreThanNextYear(skattekortPersonRequest.inntektsaar.toInt()) ->
                ValidationResult.Invalid("Inntektsår kan ikke være mer enn ${Year.now().value + 2}")

            !isNumberInputValidNumber(skattekortPersonRequest.fnr) -> ValidationResult.Invalid("Fnr er ugyldig")
            skattekortPersonRequest.fnr.length < 11 -> ValidationResult.Invalid("Fnr er mindre enn 11 siffer")
            skattekortPersonRequest.fnr.length > 11 -> ValidationResult.Invalid("Fnr er større enn 11 siffer")
            else -> ValidationResult.Valid
        }
    }
}

fun isNumberInputValidNumber(numberInput: String): Boolean {
    return numberInput.isEmpty() || numberInput.all { it.isDigit() }
}

fun isYearInputMoreThanNextYear(yearInput: Int): Boolean {
    return yearInput > Year.now().value + 1
}

fun isYearInputLessThanPreviousYear(yearInput: Int): Boolean {
    return yearInput < Year.now().value - 1
}