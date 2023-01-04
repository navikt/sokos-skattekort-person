package no.nav.sokos.skattekort.person.util

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest

fun RequestValidationConfig.validationHandler() {

    validate<SkattekortPersonRequest> { skattekortPersonRequest ->

        fun isNumeric(toCheck: String): Boolean {
            return toCheck.all { char -> char.isDigit() }
        }

        when {
            !isNumeric(skattekortPersonRequest.inntektsaar) -> ValidationResult.Invalid("Inntektår er ugyldig")
            skattekortPersonRequest.inntektsaar.toInt() < 2022 -> ValidationResult.Invalid("Inntektsår kan ikke være mindre enn 2022")
            skattekortPersonRequest.inntektsaar.toInt() > 2023 -> ValidationResult.Invalid("Inntektsår kan ikke være mer enn 2023")
            !isNumeric(skattekortPersonRequest.fnr) -> ValidationResult.Invalid("Fnr er ugyldig")
            skattekortPersonRequest.fnr.length < 11 -> ValidationResult.Invalid("Fnr er mindre enn 11 siffer")
            skattekortPersonRequest.fnr.length > 11 -> ValidationResult.Invalid("Fnr er større enn 11 siffer")
            else -> ValidationResult.Valid
        }
    }
}