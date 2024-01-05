package no.nav.sokos.skattekort.person

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import no.nav.sokos.skattekort.person.api.metricsApi
import no.nav.sokos.skattekort.person.api.naisApi
import no.nav.sokos.skattekort.person.api.swaggerApi
import no.nav.sokos.skattekort.person.config.commonConfig

const val APPLICATION_JSON = "application/json"

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
            naisApi({ applicationState.initialized }, { applicationState.running })
            metricsApi()
            swaggerApi()
        }
    }
}