package no.nav.sokos.skattekort.person.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonResponse
import no.nav.sokos.skattekort.person.service.SkattekortPersonService

fun Route.skattekortApi(
    skattekortPersonService: SkattekortPersonService = SkattekortPersonService()
) {
        route("/api/v1") {
            post("hent-skattekort") {
                val skattekortPersonRequest: SkattekortPersonRequest = call.receive()
                val response = SkattekortPersonResponse(skattekortPersonService.hentSkattekortPerson(
                    skattekortPersonRequest,
                    call
                ))
                call.respond(response)
            }
        }
}