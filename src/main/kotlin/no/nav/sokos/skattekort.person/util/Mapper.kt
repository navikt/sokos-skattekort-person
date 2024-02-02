package no.nav.sokos.skattekort.person.util

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val jsonMapper: ObjectMapper = jacksonObjectMapper().apply { customConfig() }

val xmlMapper: ObjectMapper = XmlMapper(
    JacksonXmlModule()
        .apply { setDefaultUseWrapper(false) })
    .registerKotlinModule()
    .apply { JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) }
    .registerModule(JavaTimeModule())