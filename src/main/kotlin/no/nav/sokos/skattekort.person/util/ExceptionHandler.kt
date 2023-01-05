package no.nav.sokos.skattekort.person.util

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.path
import io.ktor.server.response.respond
import java.time.ZonedDateTime

fun StatusPagesConfig.exceptionHandler() {

    exception<RequestValidationException> { call, cause ->
        call.respond(
            HttpStatusCode.BadRequest, FeilmeldingResponse(
                ZonedDateTime.now(),
                HttpStatusCode.BadRequest.value,
                HttpStatusCode.BadRequest.description,
                cause.reasons.joinToString(),
                call.request.path()
            )
        )
    }
}

data class FeilmeldingResponse(
    val timestamp: ZonedDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)