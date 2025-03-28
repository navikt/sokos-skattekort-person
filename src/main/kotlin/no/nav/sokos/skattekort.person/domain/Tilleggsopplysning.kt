package no.nav.sokos.skattekort.person.domain

import com.fasterxml.jackson.annotation.JsonProperty

enum class Tilleggsopplysning(val value: String) {
    @JsonProperty("oppholdPaaSvalbard")
    OPPHOLD_PAA_SVALBARD("oppholdPaaSvalbard"),

    @JsonProperty("kildeskattpensjonist")
    KILDESKATTPENSJONIST("kildeskattpensjonist"),

    @JsonProperty("oppholdITiltakssone")
    OPPHOLD_I_TILTAKSSONE("oppholdITiltakssone"),

    @JsonProperty("kildeskattPaaLoenn")
    KILDESKATT_PAA_LOENN("kildeskattPaaLoenn"),
}
