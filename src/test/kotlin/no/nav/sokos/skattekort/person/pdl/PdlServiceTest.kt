package no.nav.sokos.skattekort.person.pdl

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.assertThrows

import no.nav.sokos.skattekort.person.APPLICATION_JSON
import no.nav.sokos.skattekort.person.TestUtil.readFromResource
import no.nav.sokos.skattekort.person.listener.WiremockListener
import no.nav.sokos.skattekort.person.listener.WiremockListener.wiremock

internal class PdlServiceTest :
    FunSpec({

        extensions(listOf(WiremockListener))

        val pdlClientService: PdlClientService by lazy {
            PdlClientService(
                pdlUrl = wiremock.baseUrl() + "/graphql",
                accessTokenClient = WiremockListener.accessTokenClient,
            )
        }

        test("Fant person i PDL") {

            val hentPersonResponse = "pdl/hentPerson_fant_person_response.json".readFromResource()

            wiremock.stubFor(
                WireMock
                    .post(urlEqualTo("/graphql"))
                    .willReturn(
                        aResponse()
                            .withHeader(HttpHeaders.ContentType, APPLICATION_JSON)
                            .withStatus(HttpStatusCode.OK.value)
                            .withBody(hentPersonResponse),
                    ),
            )

            val result = pdlClientService.getPersonNavn("22334455667")

            result shouldNot beNull()
            result?.navn?.first()?.fornavn shouldBe "TRIVIELL"
            result?.navn?.first()?.mellomnavn shouldBe beNull()
            result?.navn?.first()?.etternavn shouldBe "SKILPADDE"
        }

        test("Fant ikke person i PDL") {

            val hentPersonResponse = "pdl/hentPerson_fant_ikke_person_response.json".readFromResource()

            wiremock.stubFor(
                WireMock
                    .post(urlEqualTo("/graphql"))
                    .willReturn(
                        aResponse()
                            .withHeader(HttpHeaders.ContentType, APPLICATION_JSON)
                            .withStatus(HttpStatusCode.OK.value)
                            .withBody(hentPersonResponse),
                    ),
            )

            val result = pdlClientService.getPersonNavn("22334455667")

            result shouldBe beNull()
        }

        test("Ikke authentisert mot PDL") {

            val hentPersonResponse = "pdl/hentPerson_ikke_authentisert_response.json".readFromResource()

            wiremock.stubFor(
                WireMock
                    .post(urlEqualTo("/graphql"))
                    .willReturn(
                        aResponse()
                            .withHeader(HttpHeaders.ContentType, APPLICATION_JSON)
                            .withStatus(HttpStatusCode.OK.value)
                            .withBody(hentPersonResponse),
                    ),
            )

            val exception =
                assertThrows<Exception> {
                    pdlClientService.getPersonNavn("22334455667")
                }

            exception shouldNotBe beNull()
            exception.message shouldBe "Feil med henting av person fra PDL: (Path: [hentPerson], Code: [unauthenticated], Message: [null])"
        }
    })
