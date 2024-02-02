package no.nav.sokos.skattekort.person.pdl

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
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import java.time.Instant
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import no.nav.sokos.skattekort.person.config.PropertiesConfig
import no.nav.sokos.skattekort.person.util.defaultHttpClient

class AccessTokenClient(
    private val azureAdClientConfig: PropertiesConfig.AzureAdClientConfig = PropertiesConfig.AzureAdClientConfig(),
    private val pdlConfig: PropertiesConfig.PdlConfig = PropertiesConfig.PdlConfig(),
    private val httpClient: HttpClient = defaultHttpClient,
    private val aadAccessTokenUrl: String = "https://login.microsoftonline.com/${azureAdClientConfig.tenantId}/oauth2/v2.0/token"
) {
    private val mutex = Mutex()

    @Volatile
    private var token: AccessToken = runBlocking { AccessToken(getAccessToken()) }

    suspend fun getSystemToken(): String {
        val expiresInToMinutes = Instant.now().plusSeconds(120L)
        return mutex.withLock {
            when {
                token.expiresAt.isBefore(expiresInToMinutes) -> {
                    token = AccessToken(getAccessToken())
                    token.accessToken
                }

                else -> token.accessToken
            }
        }
    }

    private suspend fun getAccessToken(): AzureAccessToken =
        retry {
            val response: HttpResponse = httpClient.post(aadAccessTokenUrl) {
                accept(ContentType.Application.Json)
                method = HttpMethod.Post
                setBody(FormDataContent(Parameters.build {
                    append("tenant", azureAdClientConfig.tenantId)
                    append("client_id", azureAdClientConfig.clientId)
                    append("scope", pdlConfig.pdlScope)
                    append("client_secret", azureAdClientConfig.clientSecret)
                    append("grant_type", "client_credentials")
                }))
            }

            if (response.status != HttpStatusCode.OK) {
                val message =
                    "GetAccessToken returnerte ${response.status} med feilmelding: ${response.errorMessage()}"
                throw RuntimeException(message)
            } else {
                response.body()
            }
        }
}

suspend fun HttpResponse.errorMessage(): String? =
    jacksonObjectMapper().readTree(body<String>()).get("error_description")?.asText()

private data class AzureAccessToken(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Long
)

private data class AccessToken(
    val accessToken: String,
    val expiresAt: Instant
) {
    constructor(azureAccessToken: AzureAccessToken) : this(
        accessToken = azureAccessToken.accessToken,
        expiresAt = Instant.now().plusSeconds(azureAccessToken.expiresIn)
    )
}
