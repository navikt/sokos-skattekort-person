package no.nav.sokos.skattekort.person.config

import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import java.time.ZonedDateTime

fun StatusPagesConfig.statusPageConfig() {

    exception<Throwable> { call, cause ->
        val (responseStatus, apiError) = when (cause) {

            is RequestValidationException -> {
                Pair(
                    HttpStatusCode.BadRequest, ApiError(
                        ZonedDateTime.now(),
                        HttpStatusCode.BadRequest.value,
                        HttpStatusCode.BadRequest.description,
                        cause.reasons.joinToString(),
                        call.request.path()
                    )
                )
            }

            is ClientRequestException -> {
                Pair(
                    cause.response.status, ApiError(
                        ZonedDateTime.now(),
                        cause.response.status.value,
                        cause.response.status.description,
                        cause.message,
                        call.request.path()
                    )
                )
            }

            else -> Pair(
                HttpStatusCode.InternalServerError, ApiError(
                    ZonedDateTime.now(),
                    HttpStatusCode.InternalServerError.value,
                    HttpStatusCode.InternalServerError.description,
                    cause.message ?: "En teknisk feil har oppstått. Ta kontakt med utviklerne",
                    call.request.path()
                )
            )
        }

        call.application.log.error(
            "Feilet håndtering av ${call.request.httpMethod} - ${call.request.path()} status=$responseStatus",
            cause
        )
        call.respond(responseStatus, apiError)
    }
}

data class ApiError(
    val timestamp: ZonedDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)