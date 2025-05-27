package no.nav.sokos.skattekort.person.config

import com.auth0.jwt.JWT
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.header
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import mu.KotlinLogging
import org.slf4j.MarkerFactory
import org.slf4j.event.Level

import no.nav.sokos.skattekort.person.metrics.Metrics

val TEAM_LOGS_MARKER = MarkerFactory.getMarker("TEAM_LOGS")
const val X_KALLENDE_SYSTEM = "x-kallende-system"

private val logger = KotlinLogging.logger {}

fun Application.commonConfig() {
    install(CallLogging) {
        level = Level.INFO
        mdc(X_KALLENDE_SYSTEM) { it.extractCallingSystemFromJwtToken() }
        filter { call -> call.request.path().startsWith("/api") }
        disableDefaultColors()
    }
    install(ContentNegotiation) {
        jackson {
            findAndRegisterModules()
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
    install(RequestValidation) {
        requestValidationConfig()
    }
    install(StatusPages) {
        statusPageConfig()
    }
    install(MicrometerMetrics) {
        registry = Metrics.prometheusMeterRegistry
        meterBinders =
            listOf(
                UptimeMetrics(),
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                JvmThreadMetrics(),
                ProcessorMetrics(),
            )
    }
}

private fun ApplicationCall.extractCallingSystemFromJwtToken(): String =
    request.header(HttpHeaders.Authorization)?.removePrefix("Bearer ")?.let {
        runCatching {
            JWT.decode(it)
        }.onFailure {
            logger.warn("Failed to decode token: ", it)
        }.getOrNull()
            ?.let { it.claims["azp_name"]?.asString() ?: it.claims["client_id"]?.asString() }
            ?.split(":")
            ?.last()
    } ?: "Ukjent"

fun Routing.internalNaisRoutes(
    applicationState: ApplicationState,
    readynessCheck: () -> Boolean = { applicationState.ready },
    alivenessCheck: () -> Boolean = { applicationState.alive },
) {
    route("internal") {
        get("isAlive") {
            when (alivenessCheck()) {
                true -> call.respondText { "I'm alive :)" }
                else ->
                    call.respondText(
                        text = "I'm dead x_x",
                        status = HttpStatusCode.InternalServerError,
                    )
            }
        }
        get("isReady") {
            when (readynessCheck()) {
                true -> call.respondText { "I'm ready! :)" }
                else ->
                    call.respondText(
                        text = "Wait! I'm not ready yet! :O",
                        status = HttpStatusCode.InternalServerError,
                    )
            }
        }
        get("metrics") {
            call.respondText(
                Metrics.prometheusMeterRegistry.scrape(),
            )
        }
    }
}
