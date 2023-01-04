package no.nav.sokos.skattekort.person

import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import no.nav.sokos.skattekort.person.config.Config
import no.nav.sokos.skattekort.person.config.configureCallId
import no.nav.sokos.skattekort.person.config.configureMetrics
import no.nav.sokos.skattekort.person.config.configureRequestValidation
import no.nav.sokos.skattekort.person.config.configureRouting
import no.nav.sokos.skattekort.person.config.configureSecurity
import no.nav.sokos.skattekort.person.config.configureSerialization
import no.nav.sokos.skattekort.person.config.configureStatusPages
import no.nav.sokos.skattekort.person.database.OracleDataSource
import no.nav.sokos.skattekort.person.metrics.appStateReadyFalse
import no.nav.sokos.skattekort.person.metrics.appStateRunningFalse
import no.nav.sokos.skattekort.person.service.SkattekortPersonService

fun main() {
    val applicationState = ApplicationState()
    val applicationConfiguration = Config.Configuration()
    val oracleDataSource = OracleDataSource(applicationConfiguration.databaseConfig)
    val skattekortPersonService = SkattekortPersonService(oracleDataSource)

    HttpServer(applicationState, applicationConfiguration, skattekortPersonService, oracleDataSource).start()

}

class HttpServer(
    private val applicationState: ApplicationState,
    private val applicationConfiguration: Config.Configuration,
    private val skattekortPersonService: SkattekortPersonService,
    private val oracleDataSource: OracleDataSource,
    port: Int = 8080,
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            oracleDataSource.close()
            this.stop()
        })
    }

    private val embeddedServer = embeddedServer(Netty, port) {
        configureSecurity(applicationConfiguration.azureAdConfig, applicationConfiguration.useAuthentication)
        configureSerialization()
        configureCallId()
        configureMetrics()
        configureRequestValidation()
        configureStatusPages()
        configureRouting(applicationState, skattekortPersonService, applicationConfiguration.useAuthentication)
    }

    fun start() {
        applicationState.running = true
        embeddedServer.start(wait = true)
    }
    private fun stop() {
        applicationState.running = false
        embeddedServer.stop(5, 5, TimeUnit.SECONDS)
    }
}


class ApplicationState(
    alive: Boolean = true,
    ready: Boolean = false
) {
    var initialized: Boolean by Delegates.observable(alive) { _, _, newValue ->
        if (!newValue) appStateReadyFalse.inc()
    }
    var running: Boolean by Delegates.observable(ready) { _, _, newValue ->
        if (!newValue) appStateRunningFalse.inc()
    }
}
