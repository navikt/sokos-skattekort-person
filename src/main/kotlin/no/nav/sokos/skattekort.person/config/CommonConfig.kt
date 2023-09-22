package no.nav.sokos.skattekort.person.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import java.util.UUID
import no.nav.sokos.skattekort.person.metrics.prometheusMeterRegistry
import no.nav.sokos.skattekort.person.util.exceptionHandler
import no.nav.sokos.skattekort.person.util.validationHandler
import org.slf4j.event.Level

const val SECURE_LOGGER = "secureLogger"
const val AUDIT_LOGGER = "auditLogger"

fun Application.commonConfig() {
    install(CallId) {
        header(HttpHeaders.XCorrelationId)
        generate { UUID.randomUUID().toString() }
        verify { callId: String -> callId.isNotEmpty() }
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
        validationHandler()
    }
    install(StatusPages) {
        exceptionHandler()
    }
    install(MicrometerMetrics) {
        registry = prometheusMeterRegistry
        meterBinders = listOf(
            UptimeMetrics(),
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            JvmThreadMetrics(),
            ProcessorMetrics()
        )
    }
}