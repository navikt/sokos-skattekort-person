package no.nav.sokos.skattekort.person.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.skattekort.person.ApplicationState
import no.nav.sokos.skattekort.person.api.metricsApi
import no.nav.sokos.skattekort.person.api.naisApi
import no.nav.sokos.skattekort.person.api.skattekortApi
import no.nav.sokos.skattekort.person.api.swaggerApi
import no.nav.sokos.skattekort.person.service.SkattekortPersonService

fun Application.routingConfig(
    applicationState: ApplicationState,
    skattekortPersonService: SkattekortPersonService,
    useAuthentication: Boolean
) {
    routing {
        naisApi({ applicationState.initialized }, { applicationState.running })
        metricsApi()
        swaggerApi()
        skattekortApi(skattekortPersonService, useAuthentication)
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}