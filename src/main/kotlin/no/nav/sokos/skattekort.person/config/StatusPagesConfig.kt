package no.nav.sokos.skattekort.person.config

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import no.nav.sokos.skattekort.person.util.exceptionHandler

fun Application.configureStatusPages() {
    install(StatusPages) {
        exceptionHandler()
    }
}