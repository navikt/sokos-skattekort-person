package no.nav.sokos.skattekort.person.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication

import no.nav.sokos.skattekort.person.configureTestApplication

internal class SwaggerApiTest : FunSpec({

    test("test swagger api responderer med 200 OK") {
        testApplication {
            configureTestApplication()

            val respons = client.get("/api/v1/docs")
            respons.status shouldBe HttpStatusCode.OK
        }
    }
})
