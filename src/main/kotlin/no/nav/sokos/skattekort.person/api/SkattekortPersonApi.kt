package no.nav.sokos.skattekort.person.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.sokos.skattekort.person.api.model.SkattekortPerson
import no.nav.sokos.skattekort.person.config.AUTHENTICATION_NAME
import no.nav.sokos.skattekort.person.config.authenticate
import no.nav.sokos.skattekort.person.service.SkattekortPersonService

fun Route.skattekortRoutes(
    skattekortPersonService: SkattekortPersonService,
    useAuthentication: Boolean
) {
    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("/api") {
            post("/v1/skattekort") {
                val skattekortPerson: SkattekortPerson = call.receive()
                val skattekort = skattekortPersonService.hentSkattekortPerson(skattekortPerson)
                call.respond(HttpStatusCode.OK, skattekort)
            }
        }
    }
}