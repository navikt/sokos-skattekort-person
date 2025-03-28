package no.nav.sokos.skattekort.person.util

import java.net.ProxySelector

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import org.apache.http.impl.conn.SystemDefaultRoutePlanner

fun ObjectMapper.customConfig() {
    registerModule(JavaTimeModule())
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

val defaultHttpClient =
    HttpClient(Apache) {
        expectSuccess = false
        install(ContentNegotiation) {
            jackson {
                customConfig()
            }
        }

        engine {
            customizeClient {
                setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
            }
        }
    }
