package no.nav.sokos.skattekort.person.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.sokos.skattekort.person.config.AUTHENTICATION_NAME
import no.nav.sokos.skattekort.person.config.authenticate
import no.nav.sokos.skattekort.person.service.SkattekortService

fun Route.skattekortRoutes(
    skattekortService: SkattekortService,
    useAuthentication: Boolean
) {
    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("/api") {
            post("/v1/skattekort") {
                val response = skattekortService.hentSkattekort()
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}