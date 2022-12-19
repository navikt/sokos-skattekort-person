package no.nav.sokos.skattekort.person.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.http

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson

object HttpClientConfig {
    val httpClient = HttpClient(Apache) {
        expectSuccess = false
        install(ContentNegotiation) {
            jackson {
                jsonSerializationConfig()
            }
        }

        engine {
            System.getenv("HTTP_PROXY")?.let {
                this.proxy = ProxyBuilder.http(it)
            }
        }
    }
}
