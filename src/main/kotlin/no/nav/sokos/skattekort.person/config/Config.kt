package no.nav.sokos.skattekort.person.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.sokos.skattekort.person.config.HttpClientConfig.httpClient

private val log = KotlinLogging.logger {}
object Config {

    private val defaultProperties = ConfigurationMap(
        mapOf(
            "NAIS_APP_NAME" to "sokos-skattekort-person",
            "NAIS_NAMESPACE" to "okonomi"
        )
    )

    private val localDevProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.LOCAL.toString(),
            "USE_AUTHENTICATION" to "true",
            "AZURE_APP_CLIENT_ID" to UUID.randomUUID().toString(),
            "AZURE_APP_WELL_KNOWN_URL" to "https://fakedings.dev-gcp.nais.io/default/.well-known/openid-configuration",
        )
    )

    private val devProperties = ConfigurationMap(mapOf("application.profile" to Profile.DEV.toString()))
    private val prodProperties = ConfigurationMap(mapOf("application.profile" to Profile.PROD.toString()))

    private val config = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding devProperties overriding defaultProperties
        "prod-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding prodProperties overriding defaultProperties
        else ->
            ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding ConfigurationProperties.fromOptionalFile(
            File("defaults.properties")
        ) overriding localDevProperties overriding defaultProperties
    }

    operator fun get(key: String): String = config[Key(key, stringType)]
    fun getOrNull(key: String): String? = config.getOrNull(Key(key, stringType))

    data class Configuration(
        val naisAppName: String = get("NAIS_APP_NAME"),
        val profile: Profile = Profile.valueOf(this["application.profile"]),
        val useAuthentication: Boolean = get("USE_AUTHENTICATION").toBoolean(),
        val azureAdConfig: AzureAdConfig = AzureAdConfig(),
    )

    class AzureAdConfig(
        val clientId: String = this["AZURE_APP_CLIENT_ID"],
        val wellKnownUrl: String = this["AZURE_APP_WELL_KNOWN_URL"]
    )

    data class OpenIdMetadata(
        @JsonProperty("jwks_uri") val jwksUri: String,
        @JsonProperty("issuer") val issuer: String,
        @JsonProperty("token_endpoint") val tokenEndpoint: String,
    )

    fun wellKnowConfig(wellKnownUrl: String): OpenIdMetadata {
        val openIdMetadata: OpenIdMetadata by lazy {
            runBlocking { httpClient.get(wellKnownUrl).body() }
        }
        return openIdMetadata
    }

    enum class Profile {
        LOCAL, DEV, PROD
    }
}

