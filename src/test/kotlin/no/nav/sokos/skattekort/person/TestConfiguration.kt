package no.nav.sokos.skattekort.person

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import no.nav.sokos.skattekort.person.api.metricsApi
import no.nav.sokos.skattekort.person.api.naisApi
import no.nav.sokos.skattekort.person.api.swaggerApi
import no.nav.sokos.skattekort.person.config.commonConfig

internal const val API_SKATTEKORT_PATH = "/api/v1/hent-skattekort"
internal const val APPLICATION_JSON = "application/json"

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

fun setupMockEngine(
    responsFilNavn: String,
    statusCode: HttpStatusCode = HttpStatusCode.OK,
): HttpClient {
    return HttpClient(MockEngine {
        val content = responsFilNavn.readFromResource()
        respond(
            content = content,
            headers = headersOf(HttpHeaders.ContentType, APPLICATION_JSON),
            status = statusCode
        )

    }) {
        expectSuccess = false
    }
}