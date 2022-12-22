package no.nav.sokos.skattekort.person.domain

import com.fasterxml.jackson.annotation.JsonProperty

enum class Tabelltype(val value: String) {

    @JsonProperty("trekktabellForPensjon")
    TREKKTABELL_FOR_PENSJON("trekktabellForPensjon"),

    @JsonProperty("trekktabellForLoenn")
    TREKKTABELL_FOR_LOENN("trekktabellForLoenn");

}