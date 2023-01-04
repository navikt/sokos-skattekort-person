package no.nav.sokos.skattekort.person.util

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.exceptionHandler() {

    exception<RequestValidationException> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
    }

}