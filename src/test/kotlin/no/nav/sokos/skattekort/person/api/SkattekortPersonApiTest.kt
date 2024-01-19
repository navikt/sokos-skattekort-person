package no.nav.sokos.skattekort.person.api

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.kotest.core.spec.style.FunSpec
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

        val frikortXml = "frikort.xml".readFromResource()
        val skattekortTilArbeidsgiverObject = xmlMapper.readValue(frikortXml, SkattekortTilArbeidsgiver::class.java)

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiverObject
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
    }

    test("hent skattekort med trekkprosent for gjeldende år") {

        val trekkprosentXml = "trekkprosent.xml".readFromResource()
        val skattekortTilArbeidsgiverObject =
            xmlMapper.readValue(trekkprosentXml, SkattekortTilArbeidsgiver::class.java)

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiverObject
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

    }

    test("hent skattekort med trekktabell for gjeldende år pluss 1") {

        val trekktabellXml = "trekktabell.xml".readFromResource()
        val skattekortTilArbeidsgiverObject = xmlMapper.readValue(trekktabellXml, SkattekortTilArbeidsgiver::class.java)

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiverObject
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

    }

    test("hent skattekort med status resultatPaaForespoersel 'ikkeSkattekort'") {
        val ikkeSkattekort = "ikkeSkattekort.xml".readFromResource()
        val skattekortTilArbeidsgiverObject = xmlMapper.readValue(ikkeSkattekort, SkattekortTilArbeidsgiver::class.java)

        every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns listOf(
            skattekortTilArbeidsgiverObject
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

        //response.jsonPath().getList<SkattekortTilArbeidsgiver>("arbeidsgiver").first().arbeidsgiver.first().arbeidstaker.first().resultatPaaForespoersel shouldBe Resultatstatus.IKKE_SKATTEKORT

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
            .body("message", equalTo("Fødelsnummer er ugyldig"))

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
            .body("message", equalTo("Inntektsår er ugyldig"))

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
                    "Inntektsår kan ikke være utenfor intervallet ${Year.now().minusYears(1)} til ${
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
                    "Inntektsår kan ikke være utenfor intervallet ${Year.now().minusYears(1)} til ${
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
            .body("message", equalTo("Fødelsnummer må være 11 siffer"))

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
            .body("message", equalTo("Fødelsnummer må være 11 siffer"))
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