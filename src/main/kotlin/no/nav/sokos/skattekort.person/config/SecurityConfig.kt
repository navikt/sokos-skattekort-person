package no.nav.sokos.skattekort.person.config

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import mu.KotlinLogging
import no.nav.sokos.skattekort.person.config.Config.AzureAdConfig
import no.nav.sokos.skattekort.person.config.Config.OpenIdMetadata
import no.nav.sokos.skattekort.person.config.Config.wellKnowConfig
import java.net.URL
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}
const val AUTHENTICATION_NAME = "azureAd"

fun Application.configureSecurity(
    azureAdConfig: AzureAdConfig,
    useAuthentication: Boolean = true
) {
    log.info("Use authentication: $useAuthentication")
    if (useAuthentication) {
        log.info { "start getting openIDMetadata" }
        val openIdMetadata: OpenIdMetadata = wellKnowConfig(azureAdConfig.wellKnownUrl)

        log.info { "stop getting openIDMetadata and jwksUri:  ${openIdMetadata.jwksUri}" }
        val jwkProvider = cachedJwkProvider(openIdMetadata.jwksUri)
        log.info { "start jwkProvider: ${URL(openIdMetadata.jwksUri)}" }

        log.info { "issuer: ${openIdMetadata.issuer}" }
        authentication {
            jwt(AUTHENTICATION_NAME) {
                realm = Config.Configuration().naisAppName
                log.info { "Kommer du inn under realm?" }
                verifier(
                    jwkProvider = jwkProvider,
                    issuer = openIdMetadata.issuer
                )
                log.info { "Rett etter verifier" }
                validate { credential ->
                    try {
                        requireNotNull(credential.payload.audience) {
                            log.info("Auth: Missing audience in token")
                            "Auth: Missing audience in token"
                        }
                        require(credential.payload.audience.contains(azureAdConfig.clientId)) {
                            log.info("Auth: Valid audience not found in claims")
                            "Auth: Valid audience not found in claims"
                        }
                        JWTPrincipal(credential.payload)
                    } catch (e: Exception) {
                        log.warn(e) { "Client authentication failed" }
                        null
                    }
                }
            }
        }
    }
}

private fun cachedJwkProvider(jwksUri: String): JwkProvider {
    val jwkProviderBuilder = JwkProviderBuilder(URL(jwksUri))
    System.getenv("HTTP_PROXY")?.let {
        jwkProviderBuilder.proxied(ProxyBuilder.http(it))
    }

    return jwkProviderBuilder
        .cached(10, 24, TimeUnit.HOURS) // cache up to 10 JWKs for 24 hours
        .rateLimited(10, 1, TimeUnit.MINUTES) // if not cached, only allow max 10 different keys per minute to be fetched from external provider
        .build()
}