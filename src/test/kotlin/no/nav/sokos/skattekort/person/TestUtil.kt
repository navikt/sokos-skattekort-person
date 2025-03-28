package no.nav.sokos.skattekort.person

internal const val API_SKATTEKORT_PATH = "/api/v1/hent-skattekort"
internal const val APPLICATION_JSON = "application/json"

fun String.readFromResource() = {}::class.java.classLoader.getResource(this)!!.readText()
