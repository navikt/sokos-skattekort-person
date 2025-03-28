package no.nav.sokos.skattekort.person.config

import java.net.URI
import java.util.concurrent.TimeUnit

import kotlinx.coroutines.runBlocking

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.call.body
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.client.request.get
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

private val logger = mu.KotlinLogging.logger {}
const val AUTHENTICATION_NAME = "azureAd"

fun Application.securityConfig(
    useAuthentication: Boolean,
    azureAdProperties: PropertiesConfig.AzureAdProperties = PropertiesConfig.AzureAdProperties(),
) {
    logger.info("Use authentication: $useAuthentication")
    if (useAuthentication) {
        val openIdMetadata: OpenIdMetadata = wellKnowConfig(azureAdProperties.wellKnownUrl)
        val jwkProvider = cachedJwkProvider(openIdMetadata.jwksUri)

        authentication {
            jwt(AUTHENTICATION_NAME) {
                realm = PropertiesConfig.Configuration().naisAppName
                verifier(
                    jwkProvider = jwkProvider,
                    issuer = openIdMetadata.issuer,
                )
                validate { credential ->
                    try {
                        requireNotNull(credential.payload.audience) {
                            logger.info("Auth: Missing audience in token")
                            "Auth: Missing audience in token"
                        }
                        require(credential.payload.audience.contains(azureAdProperties.clientId)) {
                            logger.info("Auth: Valid audience not found in claims")
                            "Auth: Valid audience not found in claims"
                        }
                        JWTPrincipal(credential.payload)
                    } catch (e: Exception) {
                        logger.warn(e) { "Client authentication failed" }
                        null
                    }
                }
            }
        }
    }
}

private fun cachedJwkProvider(jwksUri: String): JwkProvider {
    val jwkProviderBuilder = JwkProviderBuilder(URI(jwksUri).toURL())
    System.getenv("HTTP_PROXY")?.let {
        jwkProviderBuilder.proxied(ProxyBuilder.http(it))
    }

    return jwkProviderBuilder
        .cached(10, 24, TimeUnit.HOURS) // cache up to 10 JWKs for 24 hours
        .rateLimited(
            10,
            1,
            TimeUnit.MINUTES,
        ) // if not cached, only allow max 10 different keys per minute to be fetched from external provider
        .build()
}

data class OpenIdMetadata(
    @JsonProperty("jwks_uri") val jwksUri: String,
    @JsonProperty("issuer") val issuer: String,
    @JsonProperty("token_endpoint") val tokenEndpoint: String,
)

private fun wellKnowConfig(wellKnownUrl: String): OpenIdMetadata {
    val openIdMetadata: OpenIdMetadata by lazy {
        runBlocking { httpClient.get(wellKnownUrl).body() }
    }
    return openIdMetadata
}
