package no.nav.sokos.skattekort.person.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.skattekort.person.api.metricsRoutes
import no.nav.sokos.skattekort.person.api.naisRoutes
import no.nav.sokos.skattekort.person.api.skattekortRoutes
import no.nav.sokos.skattekort.person.service.SkattekortService
import no.nav.sokos.skattekort.person.util.ApplicationState

fun Application.configureRouting(
    applicationState: ApplicationState,
    skattekortService: SkattekortService,
    useAuthentication: Boolean
) {
    routing {
        naisRoutes({ applicationState.alive }, { applicationState.ready })
        metricsRoutes()

        skattekortRoutes(skattekortService, useAuthentication)
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}