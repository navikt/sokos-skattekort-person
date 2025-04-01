package no.nav.sokos.skattekort.person.config

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult

import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.validator.validFnr

fun RequestValidationConfig.requestValidationConfig() {
    validate<SkattekortPersonRequest> { skattekortPersonRequest ->

        when {
            !validFnr(skattekortPersonRequest.fnr) -> ValidationResult.Invalid("Fødelsnummer er ugyldig. Fødelsnummer må være 11 siffer")

/*            !validYear(skattekortPersonRequest.inntektsaar) ->
                ValidationResult.Invalid(
                    "Inntektsåret er ugyldig. Inntektsår må være mellom ${
                        Year.now().minusYears(1)
                    } til ${Year.now().plusYears(1)}",
                )*/

            else -> ValidationResult.Valid
        }
    }
}
