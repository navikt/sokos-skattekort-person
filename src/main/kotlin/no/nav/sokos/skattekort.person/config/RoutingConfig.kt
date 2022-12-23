package no.nav.sokos.skattekort.person.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.skattekort.person.ApplicationState
import no.nav.sokos.skattekort.person.api.metricsRoutes
import no.nav.sokos.skattekort.person.api.naisRoutes
import no.nav.sokos.skattekort.person.api.skattekortRoutes
import no.nav.sokos.skattekort.person.service.SkattekortPersonService

fun Application.configureRouting(
    applicationState: ApplicationState,
    skattekortPersonService: SkattekortPersonService,
    useAuthentication: Boolean
) {
    routing {
        naisRoutes({ applicationState.initialized }, { applicationState.running })
        metricsRoutes()
        skattekortRoutes(skattekortPersonService, useAuthentication)
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}