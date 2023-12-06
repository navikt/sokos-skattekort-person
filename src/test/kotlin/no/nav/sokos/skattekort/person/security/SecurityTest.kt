package no.nav.sokos.skattekort.person.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.skattekort.person.API_SKATTEKORT_PATH
import no.nav.sokos.skattekort.person.api.model.SkattekortPersonRequest
import no.nav.sokos.skattekort.person.api.skattekortApi
import no.nav.sokos.skattekort.person.config.AUTHENTICATION_NAME
import no.nav.sokos.skattekort.person.config.PropertiesConfig
import no.nav.sokos.skattekort.person.config.authenticate
import no.nav.sokos.skattekort.person.config.securityConfig
import no.nav.sokos.skattekort.person.configureTestApplication
import no.nav.sokos.skattekort.person.service.SkattekortPersonService
import no.nav.sokos.skattekort.person.util.customConfig

val skattekortPersonService: SkattekortPersonService = mockk()

class SecurityTest : FunSpec({

    test("test http POST endepunkt uten token bør returnere 401") {
        withMockOAuth2Server {
            testApplication {
                configureTestApplication()
                this.application {
                    securityConfig(authConfig())
                    routing {
                        authenticate(true, AUTHENTICATION_NAME) {
                            skattekortApi(skattekortPersonService)
                        }
                    }
                }
                val response = client.post(API_SKATTEKORT_PATH)
                response.status shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    test("test http POST endepunkt med token bør returnere 200") {
        withMockOAuth2Server {
            val mockOAuth2Server = this
            testApplication {
                val client = createClient {
                    install(ContentNegotiation) {
                        jackson {
                            customConfig()
                        }
                    }
                }
                configureTestApplication()
                this.application {
                    securityConfig(authConfig())
                    routing {
                        authenticate(true, AUTHENTICATION_NAME) {
                            skattekortApi(skattekortPersonService)
                        }
                    }
                }

                every { skattekortPersonService.hentSkattekortPerson(any(), any()) } returns emptyList()

                val response = client.post(API_SKATTEKORT_PATH) {
                    println(mockOAuth2Server.tokenFromDefaultProvider())
                    header("Authorization", "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
                    contentType(ContentType.Application.Json)
                    setBody(SkattekortPersonRequest("12345678901", "2022"))
                }

                response.status shouldBe HttpStatusCode.OK

            }
        }
    }


})

private fun MockOAuth2Server.authConfig() =
    PropertiesConfig.AzureAdConfig(
        wellKnownUrl = wellKnownUrl("default").toString(),
        clientId = "default"
    )

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