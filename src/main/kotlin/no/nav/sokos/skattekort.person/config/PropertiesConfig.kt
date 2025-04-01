package no.nav.sokos.skattekort.person.config

import java.io.File

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

object PropertiesConfig {
    private val defaultProperties =
        ConfigurationMap(
            mapOf(
                "NAIS_APP_NAME" to "sokos-skattekort-person",
                "NAIS_NAMESPACE" to "okonomi",
                "USE_AUTHENTICATION" to "true",
            ),
        )
    private val localDevProperties =
        ConfigurationMap(
            mapOf(
                "APPLICATION_PROFILE" to Profile.LOCAL.toString(),
                "USE_AUTHENTICATION" to "false",
                "DATABASE_HOST" to "10.51.9.59",
                "DATABASE_PORT" to "1521",
                "DATABASE_NAME" to "oseskatt_u4",
                "DATABASE_SCHEMA" to "oseskatt_u4",
            ),
        )
    private val devProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.DEV.toString()))
    private val prodProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.PROD.toString()))

    private val config =
        when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
            "dev-fss" ->
                ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding devProperties overriding
                    defaultProperties
            "prod-fss" ->
                ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding prodProperties overriding
                    defaultProperties
            else ->
                ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding
                    ConfigurationProperties.fromOptionalFile(
                        File("defaults.properties"),
                    ) overriding localDevProperties overriding defaultProperties
        }

    private operator fun get(key: String): String = config[Key(key, stringType)]

    fun getOrEmpty(key: String): String = config.getOrElse(Key(key, stringType), "")

    data class Configuration(
        val naisAppName: String = get("NAIS_APP_NAME"),
        val profile: Profile = Profile.valueOf(get("APPLICATION_PROFILE")),
        val useAuthentication: Boolean = getOrEmpty("USE_AUTHENTICATION").toBoolean(),
        val azureAdProperties: AzureAdProperties = AzureAdProperties(),
    )

    data class AzureAdProperties(
        val clientId: String = getOrEmpty("AZURE_APP_CLIENT_ID"),
        val wellKnownUrl: String = getOrEmpty("AZURE_APP_WELL_KNOWN_URL"),
        val tenantId: String = getOrEmpty("AZURE_APP_TENANT_ID"),
        val clientSecret: String = getOrEmpty("AZURE_APP_CLIENT_SECRET"),
    )

    data class OseskattDatabaseProperties(
        val host: String = getOrEmpty("DATABASE_HOST"),
        val port: String = getOrEmpty("DATABASE_PORT"),
        val name: String = getOrEmpty("DATABASE_NAME"),
        val schema: String = getOrEmpty("DATABASE_SCHEMA"),
        val username: String = getOrEmpty("DATABASE_USERNAME"),
        val password: String = getOrEmpty("DATABASE_PASSWORD"),
    ) {
        val jdbcUrl: String = "jdbc:oracle:thin:@$host:$port/$name"
    }

    data class PdlProperties(
        val pdlUrl: String = getOrEmpty("PDL_URL"),
        val pdlScope: String = getOrEmpty("PDL_SCOPE"),
    )

    enum class Profile {
        LOCAL,
        DEV,
        PROD,
    }
}
