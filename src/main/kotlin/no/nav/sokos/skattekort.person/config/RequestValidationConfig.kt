package no.nav.sokos.skattekort.person.config

import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.requestvalidation.ValidationResult
import java.time.Year
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.util.validFnr
import no.nav.sokos.skattekort.person.util.validInput
import no.nav.sokos.skattekort.person.util.validYear

fun RequestValidationConfig.requestValidationConfig() {
    validate<SkattekortPersonRequest> { skattekortPersonRequest ->

        when {
            !validInput(skattekortPersonRequest.inntektsaar) -> ValidationResult.Invalid("Inntektsår er ugyldig")

            validYear(skattekortPersonRequest.inntektsaar) ->
                ValidationResult.Invalid("Inntektsår kan ikke være utenfor intervallet ${Year.now().value - 1} til ${Year.now().value + 1}")

            !validInput(skattekortPersonRequest.fnr) -> ValidationResult.Invalid("Fødelsnummer er ugyldig")
            !validFnr(skattekortPersonRequest.fnr) -> ValidationResult.Invalid("Fødelsnummer må være 11 siffer")
            else -> ValidationResult.Valid
        }
    }
}