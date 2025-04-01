package no.nav.sokos.skattekort.person.metrics

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

private const val METRICS_NAMESPACE = "sokos_skattekort_person"

object Metrics {
    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
}
