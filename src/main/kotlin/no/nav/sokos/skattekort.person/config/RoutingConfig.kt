package no.nav.sokos.skattekort.person.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.skattekort.person.ApplicationState
import no.nav.sokos.skattekort.person.api.skattekortApi
import no.nav.sokos.skattekort.person.api.swaggerApi

fun Application.routingConfig(
    applicationState: ApplicationState,
    useAuthentication: Boolean
) {
    routing {
        internalRoutes(applicationState)
        swaggerApi()
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            skattekortApi()
        }
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}