package no.nav.sokos.skattekort.person.api

import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Routing

fun Routing.swaggerRoutes() {
    swaggerUI(path = "api/v1/docs", swaggerFile = "specs/sokos-skattekort-person-v1-swagger.json")
}