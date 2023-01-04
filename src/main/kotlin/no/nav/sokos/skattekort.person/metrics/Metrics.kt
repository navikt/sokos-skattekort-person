package no.nav.sokos.skattekort.person.metrics

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.Counter

private const val METRICS_NAMESPACE = "sokos_skattekort_person"

val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

val appStateRunningFalse: Counter = Counter.build()
    .namespace(METRICS_NAMESPACE)
    .name("app_state_running_false")
    .help("app state running changed to false")
    .register(prometheusMeterRegistry.prometheusRegistry)

val appStateReadyFalse: Counter = Counter.build()
    .namespace(METRICS_NAMESPACE)
    .name("app_state_ready_false")
    .help("app state ready changed to false")
    .register(prometheusMeterRegistry.prometheusRegistry)

val databaseFailureCounter: Counter = Counter.build()
    .namespace(METRICS_NAMESPACE)
    .name("database_failure_counter")
    .labelNames("errorCode", "sqlState")
    .help("Count database errors")
    .register(prometheusMeterRegistry.prometheusRegistry)

