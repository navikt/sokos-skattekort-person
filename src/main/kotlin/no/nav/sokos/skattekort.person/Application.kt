package no.nav.sokos.skattekort.person

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.sokos.skattekort.person.config.PropertiesConfig
import no.nav.sokos.skattekort.person.config.commonConfig
import no.nav.sokos.skattekort.person.config.routingConfig
import no.nav.sokos.skattekort.person.config.securityConfig

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

private fun Application.module(){
    val applicationConfiguration = PropertiesConfig.Configuration()
    val applicationState = ApplicationState()
    applicationLifecycleConfig(applicationState)
    commonConfig()
    securityConfig(applicationConfiguration.azureAdConfig, applicationConfiguration.useAuthentication)
    routingConfig(applicationState, applicationConfiguration.useAuthentication)
}


fun Application.applicationLifecycleConfig(applicationState: ApplicationState) {
    monitor.subscribe(ApplicationStarted) {
        applicationState.ready = true
    }

    monitor.subscribe(ApplicationStopped) {
        applicationState.ready = false
    }
}

class ApplicationState(
    var ready: Boolean = true,
    var alive: Boolean = true,
)
