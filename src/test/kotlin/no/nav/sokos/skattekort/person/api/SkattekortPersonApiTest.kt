package no.nav.sokos.skattekort.person.api

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.routing
import io.mockk.every
import io.mockk.mockk
import io.restassured.RestAssured
import java.time.Year
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.sokos.skattekort.person.API_SKATTEKORT_PATH
import no.nav.sokos.skattekort.person.APPLICATION_JSON
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.config.authenticate
import no.nav.sokos.skattekort.person.config.commonConfig
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.readFromResource
import no.nav.sokos.skattekort.person.service.SkattekortPersonService
import no.nav.sokos.skattekort.person.util.xmlMapper
import org.hamcrest.Matchers.equalTo

internal const val PORT = 9090

lateinit var server: NettyApplicationEngine

val validationFilter = OpenApiValidationFilter("openapi/sokos-skattekort-person-v1-swagger.yaml")
val skattekortPersonService: SkattekortPersonService = mockk()
val mockOAuth2Server = MockOAuth2Server()


internal class SkattekortPersonApiTest : FunSpec({

    beforeEach {
        server = embeddedServer(Netty, PORT, module = Application::myApplicationModule).start()
    }

    afterEach {
        server.stop(1000, 10000)
    }

    test("hent skattekort med frikort for gjeldende år minus 1") {

        val frikortXml = "xml/frikort.xml".readFromResource()
        val skattekortTilArbeidsgiver = xmlMapper.readValue(frikortXml, SkattekortTilArbeidsgiver::class.java)
        skattekortTilArbeidsgiver.navn = "Test Testesen"

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiver
        )

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().minusYears(1)}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.jsonPath().getList<String>("navn").first().shouldBe("Test Testesen")
        response.jsonPath().getList<String>("arbeidsgiver[0].arbeidstaker[0].skattekort.forskuddstrekk[0].type").first()
            .shouldBe("Frikort")
    }

    test("hent skattekort med trekkprosent for gjeldende år") {

        val trekkprosentXml = "xml/trekkprosent.xml".readFromResource()
        val skattekortTilArbeidsgiver =
            xmlMapper.readValue(trekkprosentXml, SkattekortTilArbeidsgiver::class.java)
        skattekortTilArbeidsgiver.navn = "Ola Nordmann"

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiver
        )

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now()}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.jsonPath().getList<String>("navn").first().shouldBe("Ola Nordmann")
        response.jsonPath().getList<String>("arbeidsgiver[0].arbeidstaker[0].skattekort.forskuddstrekk[0].type").first()
            .shouldBe("Trekkprosent")

    }

    test("hent skattekort med trekktabell for gjeldende år pluss 1") {

        val trekktabellXml = "xml/trekktabell.xml".readFromResource()
        val skattekortTilArbeidsgiver = xmlMapper.readValue(trekktabellXml, SkattekortTilArbeidsgiver::class.java)

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiver
        )

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().plusYears(1)}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.jsonPath().getList<String>("navn").first().shouldBeNull()
        response.jsonPath().getList<String>("arbeidsgiver[0].arbeidstaker[0].skattekort.forskuddstrekk[0].type").first()
            .shouldBe("Trekktabell")


    }

    test("hent skattekort med status resultatPaaForespoersel 'ikkeSkattekort'") {
        val ikkeSkattekort = "xml/ikkeSkattekort.xml".readFromResource()
        val skattekortTilArbeidsgiver = xmlMapper.readValue(ikkeSkattekort, SkattekortTilArbeidsgiver::class.java)

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiver
        )

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
            .body(SkattekortPersonRequest(fnr = "11111111111", inntektsaar = "${Year.now()}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        println(response.prettyPrint())

        response.jsonPath().getList<String>("arbeidsgiver[0].arbeidstaker[0].resultatPaaForespoersel").first()
            .shouldBe("ikkeSkattekort")
    }

    test("hent skattekort med ugyldig fnr") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "dummyFnr", inntektsaar = "${Year.now()}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Fødelsnummer er ugyldig. Fødelsnummer må være 11 siffer"))

    }

    test("hent skattekort med ugyldig inntektsaar") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "dummyInntektsaar").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Inntektsåret er ugyldig. Inntektsår må være mellom ${Year.now().minusYears(1)} til ${
                Year.now().plusYears(1)
            }"))

    }

    test("hent skattekort for mindre enn nåværende år minus 1") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().minusYears(2)}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body(
                "message",
                equalTo(
                    "Inntektsåret er ugyldig. Inntektsår må være mellom ${Year.now().minusYears(1)} til ${
                        Year.now().plusYears(1)
                    }"
                )
            )

    }

    test("hent skattekort for mer enn nåværende år pluss 1") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().plusYears(2)}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body(
                "message",
                equalTo(
                    "Inntektsåret er ugyldig. Inntektsår må være mellom ${Year.now().minusYears(1)} til ${
                        Year.now().plusYears(1)
                    }"
                )
            )

    }

    test("hent skattekort ved å sende mindre enn 11 siffer fødelsnummer") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "1234567890", inntektsaar = "${Year.now()}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Fødelsnummer er ugyldig. Fødelsnummer må være 11 siffer"))

    }

    test("hent skattekort ved å sende mer enn 11 siffer fødelsnummer") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "123456789012", inntektsaar = "${Year.now()}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Fødelsnummer er ugyldig. Fødelsnummer må være 11 siffer"))
    }

})

private fun Application.myApplicationModule() {
    commonConfig()
    routing {
        authenticate(false) {
            skattekortApi(skattekortPersonService)
        }
    }
}

private fun MockOAuth2Server.tokenFromDefaultProvider() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            claims = mapOf(
                "NAVident" to "Z123456"
            )
        )
    ).serialize()