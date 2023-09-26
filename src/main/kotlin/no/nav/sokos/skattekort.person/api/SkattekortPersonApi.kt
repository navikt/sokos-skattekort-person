package no.nav.sokos.skattekort.person.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mu.KotlinLogging
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonResponse
import no.nav.sokos.skattekort.person.config.AUTHENTICATION_NAME
import no.nav.sokos.skattekort.person.config.SECURE_LOGGER
import no.nav.sokos.skattekort.person.config.authenticate
import no.nav.sokos.skattekort.person.security.getSaksbehandler
import no.nav.sokos.skattekort.person.service.SkattekortPersonService

private val logger = KotlinLogging.logger {}
private val secureLogger = KotlinLogging.logger(SECURE_LOGGER)

fun Route.skattekortApi(
    skattekortPersonService: SkattekortPersonService,
    useAuthentication: Boolean
) {
    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("/api/v1") {
            post("hent-skattekort") {
                val skattekortPersonRequest: SkattekortPersonRequest = call.receive()
                logger.info("Henter skattekort")
                secureLogger.info("Henter skattekort for: ${skattekortPersonRequest.toJson()}")
                val saksbehandler = getSaksbehandler(call)
                val response = SkattekortPersonResponse(skattekortPersonService.hentSkattekortPerson(
                    skattekortPersonRequest,
                    saksbehandler
                ))
                secureLogger.info("Returnerer følgende respons: ${response.toJson()}")
                call.respond(response)
            }
        }
    }
}