package no.nav.sokos.skattekort.person.config

import java.util.UUID

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.statuspages.StatusPages
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
import io.prometheus.client.exporter.common.TextFormat
import org.slf4j.event.Level

import no.nav.sokos.skattekort.person.ApplicationState
import no.nav.sokos.skattekort.person.metrics.Metrics

fun Application.commonConfig() {
    install(CallId) {
        header(HttpHeaders.XCorrelationId)
        generate { UUID.randomUUID().toString() }
    }
    install(CallLogging) {
        level = Level.INFO
        callIdMdc(HttpHeaders.XCorrelationId)
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

fun Routing.internalRoutes(
    applicationState: ApplicationState,
    readinessCheck: () -> Boolean = { applicationState.ready },
    alivenessCheck: () -> Boolean = { applicationState.alive },
) {
    route("internal") {
        get("isAlive") {
            healthCheckResponse(alivenessCheck(), call, "I'm alive :)", "I'm dead x_x")
        }
        get("isReady") {
            healthCheckResponse(readinessCheck(), call, "I'm ready! :)", "Wait! I'm not ready yet! :O")
        }
        get("metrics") {
            call.respondText(ContentType.parse(TextFormat.CONTENT_TYPE_004)) { Metrics.prometheusMeterRegistry.scrape() }
        }
    }
}

private val healthCheckResponse: suspend (Boolean, ApplicationCall, String, String) -> Unit =
    { isHealthy, call, successMessage, failureMessage ->
        when (isHealthy) {
            true -> call.respondText { successMessage }
            else -> call.respondText(failureMessage, status = HttpStatusCode.InternalServerError)
        }
    }
