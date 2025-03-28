package no.nav.sokos.skattekort.person.api

import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Routing

fun Routing.swaggerApi() {
    swaggerUI(path = "api/v1/docs", swaggerFile = "openapi/sokos-skattekort-person-v1-swagger.yaml")
}
