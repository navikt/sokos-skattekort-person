package no.nav.sokos.skattekort.person.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import no.nav.sokos.skattekort.person.config.Config

class OracleDataSource(
    private val oseskattDatabaseConfig: Config.OseskattDatabaseConfig
) {
    private val dataSource: HikariDataSource = HikariDataSource(hikariConfig())
    val connection: Connection get() = dataSource.connection
    fun close() = dataSource.close()

    private fun hikariConfig() = HikariConfig().apply {
        driverClassName = "oracle.jdbc.OracleDriver"
        poolName = "HikariPool-OSESKATT"
        jdbcUrl = oseskattDatabaseConfig.jdbcUrl
        username = oseskattDatabaseConfig.username
        password = oseskattDatabaseConfig.password
        maximumPoolSize = 10
        isAutoCommit = true
    }
}