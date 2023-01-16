package no.nav.sokos.skattekort.person.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import no.nav.sokos.skattekort.person.config.PropertiesConfig

class OracleDataSource(
    private val oseskattDatabaseConfig: PropertiesConfig.OseskattDatabaseConfig
) {
    private val dataSource: HikariDataSource = HikariDataSource(hikariConfig())
    val connection: Connection get() = dataSource.connection
    fun close() = dataSource.close()

    private fun hikariConfig() = HikariConfig().apply {
        driverClassName = oseskattDatabaseConfig.jdbcDriver
        poolName = oseskattDatabaseConfig.poolName
        jdbcUrl = oseskattDatabaseConfig.jdbcUrl
        username = oseskattDatabaseConfig.username
        password = oseskattDatabaseConfig.password
        schema = oseskattDatabaseConfig.schema
        maximumPoolSize = 10
        isAutoCommit = true
    }
}