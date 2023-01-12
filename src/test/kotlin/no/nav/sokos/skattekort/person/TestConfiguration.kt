package no.nav.sokos.skattekort.person

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import no.nav.sokos.skattekort.person.api.metricsRoutes
import no.nav.sokos.skattekort.person.api.naisRoutes
import no.nav.sokos.skattekort.person.api.swaggerRoutes
import no.nav.sokos.skattekort.person.config.commonConfig

const val APPLICATION_JSON = "application/json"

fun String.readFromResource() = {}::class.java.classLoader.getResource(this)!!.readText()
fun Any.toJson() = jsonMapper().writeValueAsString(this)!!

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

private fun jsonMapper() : ObjectMapper = jacksonObjectMapper().apply {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    findAndRegisterModules()
}