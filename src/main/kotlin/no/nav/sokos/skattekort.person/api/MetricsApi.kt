package no.nav.sokos.skattekort.person.api

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.prometheus.client.exporter.common.TextFormat
import no.nav.sokos.skattekort.person.metrics.Metrics

fun Routing.metricsApi() {
    route("metrics") {
        get {
            call.respondText(ContentType.parse(TextFormat.CONTENT_TYPE_004)) { Metrics.prometheusMeterRegistry.scrape() }
        }
    }
}
