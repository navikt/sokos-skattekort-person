package no.nav.sokos.skattekort.person.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object DatabaseConfig {
    val oracleDataSource: HikariDataSource by lazy {
        HikariDataSource(oracleHikariConfig())
    }

    init {
        Runtime.getRuntime().addShutdownHook(
            Thread {
                oracleDataSource.close()
            },
        )
    }

    private fun oracleHikariConfig(): HikariConfig {
        val oseskattDatabaseProperties: PropertiesConfig.OseskattDatabaseProperties = PropertiesConfig.OseskattDatabaseProperties()
        return HikariConfig().apply {
            maximumPoolSize = 10
            driverClassName = "oracle.jdbc.OracleDriver"
            poolName = "HikariPool-OSESKATT"
            jdbcUrl = oseskattDatabaseProperties.jdbcUrl
            username = oseskattDatabaseProperties.username
            password = oseskattDatabaseProperties.password
            schema = oseskattDatabaseProperties.schema
            maximumPoolSize = 10
            isAutoCommit = true
        }
    }
}
