package no.nav.sokos.skattekort.person.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import no.nav.sokos.skattekort.person.configureTestApplication

internal class ServerApiTest : FunSpec({

    test("test nais responderer med 200 OK") {

        testApplication {
            configureTestApplication()

            val isAliveResponse = client.get("/internal/isAlive")
            isAliveResponse.status shouldBe HttpStatusCode.OK

            val isReadyResponse = client.get("/internal/isReady")
            isReadyResponse.status shouldBe HttpStatusCode.OK

        }
    }

})