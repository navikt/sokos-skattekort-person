package no.nav.sokos.skattekort.person

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import no.nav.sokos.skattekort.person.api.metricsRoutes
import no.nav.sokos.skattekort.person.api.naisRoutes
import no.nav.sokos.skattekort.person.api.swaggerRoutes
import no.nav.sokos.skattekort.person.config.commonConfig

fun String.readFromResource() = {}::class.java.classLoader.getResource(this)!!.readText()

fun ApplicationTestBuilder.configureTestApplication() {
    val mapApplicationConfig = MapApplicationConfig()
    environment {
        config = mapApplicationConfig
    }

    application {
        commonConfig()
        val applicationState = ApplicationState(ready = true)

        routing {
            naisRoutes({ applicationState.initialized }, { applicationState.running })
            metricsRoutes()
            swaggerRoutes()
        }
    }
}