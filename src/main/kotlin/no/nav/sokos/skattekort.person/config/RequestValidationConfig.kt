package no.nav.sokos.skattekort.person.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import no.nav.sokos.skattekort.person.util.validationHandler

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validationHandler()
    }
}
