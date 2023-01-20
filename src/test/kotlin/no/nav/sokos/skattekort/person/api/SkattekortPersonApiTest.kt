package no.nav.sokos.skattekort.person.api

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.kotest.core.spec.style.FunSpec
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
import no.nav.sokos.skattekort.person.APPLICATION_JSON
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonResponse
import no.nav.sokos.skattekort.person.config.commonConfig
import no.nav.sokos.skattekort.person.domain.SkattekortTilArbeidsgiver
import no.nav.sokos.skattekort.person.readFromResource
import no.nav.sokos.skattekort.person.service.SkattekortPersonService
import no.nav.sokos.skattekort.person.toJson
import no.nav.sokos.skattekort.person.util.xmlMapper
import org.hamcrest.Matchers.equalTo

internal const val API_SKATTEKORT_PATH = "/api/v1/hent-skattekort"
internal const val PORT = 9090

lateinit var server: NettyApplicationEngine

val validationFilter = OpenApiValidationFilter("openapi/sokos-skattekort-person-v1-swagger.yaml")
val skattekortPersonService: SkattekortPersonService = mockk()

internal class SkattekortPersonApiTest : FunSpec({

    beforeEach {
        server = embeddedServer(Netty, PORT, module = Application::myApplicationModule).start()
    }

    afterEach {
        server.stop(1000, 10000)
    }

    test("hent skattekort med frikort") {

        val frikortXml = "frikort.xml".readFromResource()
        val skattekortTilArbeidsgiverObject = xmlMapper.readValue(frikortXml, SkattekortTilArbeidsgiver::class.java)
        val skattekortPersonResponseObject = SkattekortPersonResponse(listOf(skattekortTilArbeidsgiverObject))

        every { skattekortPersonService.hentSkattekortPerson(any()) } returns skattekortPersonResponseObject.skattekortListe

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().value - 1}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.body.`as`(SkattekortPersonResponse::class.java) shouldBe skattekortPersonResponseObject

    }

    test("hent skattekort med trekkprosent") {

        val trekkprosentXml = "trekkprosent.xml".readFromResource()
        val skattekortTilArbeidsgiverObject =
            xmlMapper.readValue(trekkprosentXml, SkattekortTilArbeidsgiver::class.java)
        val skattekortPersonResponseObject = SkattekortPersonResponse(listOf(skattekortTilArbeidsgiverObject))

        every { skattekortPersonService.hentSkattekortPerson(any()) } returns skattekortPersonResponseObject.skattekortListe

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().value}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.body.`as`(SkattekortPersonResponse::class.java) shouldBe skattekortPersonResponseObject
    }

    test("hent skattekort med trekktabell") {

        val trekktabellXml = "trekktabell.xml".readFromResource()
        val skattekortTilArbeidsgiverObject = xmlMapper.readValue(trekktabellXml, SkattekortTilArbeidsgiver::class.java)
        val skattekortPersonResponseObject = SkattekortPersonResponse(listOf(skattekortTilArbeidsgiverObject))

        every { skattekortPersonService.hentSkattekortPerson(any()) } returns skattekortPersonResponseObject.skattekortListe

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().value}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.body.`as`(SkattekortPersonResponse::class.java) shouldBe skattekortPersonResponseObject
    }

    test("hent skattekort med ugyldig fnr") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "dummyFnr", inntektsaar = "${Year.now().value}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Fnr er ugyldig"))

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
            .body("message", equalTo("Inntektår er ugyldig"))

    }

    test("hent skattekort for mindre enn nåværende år minus 1") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().value - 2}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Inntektsår kan ikke være mindre enn ${Year.now().value - 1}"))

    }

    test("hent skattekort for mer enn nåværende år") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "12345678901", inntektsaar = "${Year.now().value + 1}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Inntektsår kan ikke være mer enn ${Year.now().value}"))

    }

    test("hent skattekort ved å sende mindre enn 11 siffer fødelsnummer") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "1234567890", inntektsaar = "${Year.now().value}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Fnr er mindre enn 11 siffer"))

    }

    test("hent skattekort ved å sende mer enn 11 siffer fødelsnummer") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer dummytoken")
            .body(SkattekortPersonRequest(fnr = "123456789012", inntektsaar = "${Year.now().value}").toJson())
            .port(PORT)
            .post(API_SKATTEKORT_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo("Fnr er større enn 11 siffer"))
    }

})

private fun Application.myApplicationModule() {
    commonConfig()
    routing {
        skattekortApi(skattekortPersonService, false)
    }
}