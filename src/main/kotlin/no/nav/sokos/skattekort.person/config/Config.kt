package no.nav.sokos.skattekort.person.config

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object Config {

    data class Configuration(
        val naisAppName: String = readProperty("NAIS_APP_NAME"),
        val useAuthentication: Boolean = readProperty("USE_AUTHENTICATION", default = "true") != "false",
        val azureAdConfig: AzureAdConfig = AzureAdConfig(),
        val databaseConfig: OseskattDatabaseConfig = OseskattDatabaseConfig()
    )

    data class AzureAdConfig(
        val clientId: String = readProperty("AZURE_APP_CLIENT_ID", ""),
        val wellKnownUrl: String = readProperty("AZURE_APP_WELL_KNOWN_URL", "")
    )

    data class OseskattDatabaseConfig(
        val host: String = readProperty("DATABASE_HOST"),
        val port: String = readProperty("DATABASE_PORT"),
        val name: String = readProperty("DATABASE_NAME"),
        val username: String = readProperty("DATABASE_USERNAME"),
        val password: String = readProperty("DATABASE_PASSWORD")
    ) {
        val jdbcUrl: String = "jdbc:oracle:thin:@$host:$port/$name"
    }

    private fun readProperty(name: String, default: String? = null) =
        System.getenv(name)
            ?: System.getProperty(name)
            ?: default.takeIf { it != null }?.also { logger.info("Bruker default verdi for property $name") }
            ?: throw RuntimeException("Mandatory property '$name' was not found")
}

