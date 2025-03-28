package no.nav.sokos.skattekort.person.metrics

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.Counter

private const val METRICS_NAMESPACE = "sokos_skattekort_person"

object Metrics {
    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val databaseFailureCounter: Counter =
        Counter
            .build()
            .namespace(METRICS_NAMESPACE)
            .name("database_failure_counter")
            .labelNames("errorCode", "sqlState")
            .help("Count database errors")
            .register(prometheusMeterRegistry.prometheusRegistry)
}
