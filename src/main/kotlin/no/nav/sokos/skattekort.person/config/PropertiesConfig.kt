package no.nav.sokos.skattekort.person.config

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import java.io.File

object PropertiesConfig {

    private val defaultProperties = ConfigurationMap(
        mapOf(
            "NAIS_APP_NAME" to "sokos-skattekort-person",
            "NAIS_NAMESPACE" to "okonomi",
        )
    )
    private val localDevProperties = ConfigurationMap(
        mapOf(
            "USE_AUTHENTICATION" to "true",
            "APPLICATION_PROFILE" to Profile.LOCAL.toString(),
            "DATABASE_HOST" to "databaseHost",
            "DATABASE_PORT" to "databasePort",
            "DATABASE_NAME" to "databaseName",
            "DATABASE_SCHEMA" to "databaseSchema",
            "DATABASE_USERNAME" to "databaseUsername",
            "DATABASE_PASSWORD" to "databasePassword",
            "AZURE_APP_CLIENT_ID" to "azure-app-client-id",
            "AZURE_APP_WELL_KNOWN_URL" to "azure-app-well-known-url",
        )
    )
    private val devProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.DEV.toString()))
    private val prodProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.PROD.toString()))

    private val config = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding devProperties overriding defaultProperties
        "prod-fss" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding prodProperties overriding defaultProperties
        else ->
            ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding ConfigurationProperties.fromOptionalFile(
                File("defaults.properties")
            ) overriding localDevProperties overriding defaultProperties
    }

    private operator fun get(key: String): String = config[Key(key, stringType)]

    data class Configuration(
        val naisAppName: String = get("NAIS_APP_NAME"),
        val profile: Profile = Profile.valueOf(this["APPLICATION_PROFILE"]),
        val useAuthentication: Boolean = get("USE_AUTHENTICATION").toBoolean(),
        val azureAdConfig: AzureAdConfig = AzureAdConfig(),
        val databaseConfig: OseskattDatabaseConfig = OseskattDatabaseConfig()
    )

    data class AzureAdConfig(
        val clientId: String = this["AZURE_APP_CLIENT_ID"],
        val wellKnownUrl: String = this["AZURE_APP_WELL_KNOWN_URL"]
    )

    data class OseskattDatabaseConfig(
        val host: String = get("DATABASE_HOST"),
        val port: String = get("DATABASE_PORT"),
        val name: String = get("DATABASE_NAME"),
        val schema: String = get("DATABASE_SCHEMA"),
        val username: String = get("DATABASE_USERNAME"),
        val password: String = get("DATABASE_PASSWORD"),
        val jdbcDriver: String = "oracle.jdbc.OracleDriver",
        val poolName: String = "HikariPool-OSESKATT"
    ) {
        val jdbcUrl: String = "jdbc:oracle:thin:@$host:$port/$name"
    }

    data class AzureAdClientConfig(
        val clientId: String = get("AZURE_APP_CLIENT_ID"),
        val wellKnownUrl: String = get("AZURE_APP_WELL_KNOWN_URL"),
        val tenantId: String = get("AZURE_APP_TENANT_ID"),
        val clientSecret: String = get("AZURE_APP_CLIENT_SECRET"),
    )

    data class PdlConfig(
        val pdlHost: String = get("PDL_HOST"),
        val pdlScope: String = get("PDL_SCOPE")
    )

    enum class Profile {
        LOCAL, DEV, PROD
    }

}

