package no.nav.sokos.skattekort.person.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            jsonSerializationConfig()
        }
    }
}

fun ObjectMapper.jsonSerializationConfig() {
    findAndRegisterModules()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    enable(SerializationFeature.INDENT_OUTPUT)
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}

val jsonMapper: ObjectMapper = jacksonObjectMapper().apply { jsonSerializationConfig() }

val xmlMapper: ObjectMapper = XmlMapper(JacksonXmlModule()
    .apply { setDefaultUseWrapper(false) })
    .registerKotlinModule()
    .apply { JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) }
    .registerModule( JavaTimeModule() )

