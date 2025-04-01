package no.nav.sokos.skattekort.person.security

import java.time.Instant

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import mu.KotlinLogging

import no.nav.sokos.skattekort.person.config.PropertiesConfig
import no.nav.sokos.skattekort.person.config.httpClient

private val logger = KotlinLogging.logger {}

class AccessTokenClient(
    private val azureAdProperties: PropertiesConfig.AzureAdProperties = PropertiesConfig.AzureAdProperties(),
    private val azureAdScope: String,
    private val client: HttpClient = httpClient,
    private val azureAdAccessTokenUrl: String = "https://login.microsoftonline.com/${azureAdProperties.tenantId}/oauth2/v2.0/token",
) {
    private val mutex = Mutex()

    @Volatile
    private var token: AccessToken = runBlocking { AccessToken(getAccessToken()) }

    suspend fun hentAccessToken(): String {
        val omToMinutter = Instant.now().plusSeconds(120L)
        return mutex.withLock {
            when {
                token.expiresAt.isBefore(omToMinutter) -> {
                    logger.info("Henter ny accesstoken")
                    token = AccessToken(getAccessToken())
                    token.accessToken
                }

                else -> token.accessToken.also { logger.info("Henter accesstoken fra cache") }
            }
        }
    }

    private suspend fun getAccessToken(): AzureAccessToken {
        val response: HttpResponse =
            client.post(azureAdAccessTokenUrl) {
                accept(ContentType.Application.Json)
                method = HttpMethod.Post
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("tenant", azureAdProperties.tenantId)
                            append("client_id", azureAdProperties.clientId)
                            append("scope", azureAdScope)
                            append("client_secret", azureAdProperties.clientSecret)
                            append("grant_type", "client_credentials")
                        },
                    ),
                )
            }

        return when {
            response.status.isSuccess() -> response.body()

            else -> {
                val errorMessage =
                    "GetAccessToken returnerte ${response.status} med feilmelding: ${response.errorMessage()}"
                logger.error { errorMessage }
                throw RuntimeException(errorMessage)
            }
        }
    }
}

suspend fun HttpResponse.errorMessage(): String? = jacksonObjectMapper().readTree(body<String>()).get("error_description")?.asText()

private data class AzureAccessToken(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
)

private data class AccessToken(
    val accessToken: String,
    val expiresAt: Instant,
) {
    constructor(azureAccessToken: AzureAccessToken) : this(
        accessToken = azureAccessToken.accessToken,
        expiresAt = Instant.now().plusSeconds(azureAccessToken.expiresIn),
    )
}
