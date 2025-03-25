package no.nav.sokos.skattekort.person.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

import no.nav.sokos.skattekort.person.database.RepositoryExtensions.Parameter
import no.nav.sokos.skattekort.person.metrics.Metrics

object RepositoryExtensions {
    inline fun <R> Connection.useAndHandleErrors(block: (Connection) -> R): R {
        try {
            use {
                return block(this)
            }
        } catch (ex: SQLException) {
            Metrics.databaseFailureCounter.labels("${ex.errorCode}", ex.sqlState).inc()
            throw ex
        }
    }

    fun interface Parameter {
        fun addToPreparedStatement(
            sp: PreparedStatement,
            index: Int,
        )
    }

    fun param(value: String?) = Parameter { sp: PreparedStatement, index: Int -> sp.setString(index, value) }

    fun PreparedStatement.withParameters(vararg parameters: Parameter?) =
        apply {
            var index = 1
            parameters.forEach { it?.addToPreparedStatement(this, index++) }
        }

    fun <T> ResultSet.toList(mapper: ResultSet.() -> T) =
        mutableListOf<T>().apply {
            while (next()) {
                add(mapper())
            }
        }
}
