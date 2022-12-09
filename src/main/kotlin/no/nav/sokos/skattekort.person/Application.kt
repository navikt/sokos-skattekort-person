package no.nav.sokos.skattekort.person

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import mu.KotlinLogging
import no.nav.sokos.skattekort.person.config.Config
import no.nav.sokos.skattekort.person.config.configureCallId
import no.nav.sokos.skattekort.person.config.configureMetrics
import no.nav.sokos.skattekort.person.config.configureRouting
import no.nav.sokos.skattekort.person.config.configureSecurity
import no.nav.sokos.skattekort.person.config.configureSerialization
import no.nav.sokos.skattekort.person.service.SkattekortService
import no.nav.sokos.skattekort.person.util.ApplicationState

val log = KotlinLogging.logger {}

fun Application.start() {
    val configuration = Config.Configuration()

    log.info { "Environment: ${configuration.profile}"}
    log.info { "well-known: ${configuration.azureAdConfig.wellKnownUrl}"}

    val applicationState = ApplicationState()

    val skattekortService = SkattekortService()

    configureSecurity(configuration.azureAdConfig, configuration.useAuthentication)
    configureSerialization()
    configureCallId()
    configureMetrics()
    configureRouting(applicationState, skattekortService, configuration.useAuthentication)

    applicationState.ready = true
}

fun main(args: Array<String>): Unit = EngineMain.main(args)
