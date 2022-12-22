package no.nav.sokos.skattekort.person.domain

import com.fasterxml.jackson.annotation.JsonProperty


enum class Trekkode(val value: String) {

    @JsonProperty("loennFraHovedarbeidsgiver")
    LOENN_FRA_HOVEDARBEIDSGIVER("loennFraHovedarbeidsgiver"),

    @JsonProperty("loennFraBiarbeidsgiver")
    LOENN_FRA_BIARBEIDSGIVER("loennFraBiarbeidsgiver"),

    @JsonProperty("loennFraNAV")
    LOENN_FRA_NAV("loennFraNAV"),

    @JsonProperty("pensjon")
    PENSJON("pensjon"),

    @JsonProperty("pensjonFraNAV")
    PENSJON_FRA_NAV("pensjonFraNAV"),

    @JsonProperty("loennTilUtenrikstjenestemann")
    LOENN_TIL_UTENRIKSTJENESTEMANN("loennTilUtenrikstjenestemann"),

    @JsonProperty("loennKunTrygdeavgiftTilUtenlandskBorger")
    LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER("loennKunTrygdeavgiftTilUtenlandskBorger"),

    @JsonProperty("loennKunTrygdeavgiftTilUtenlandskBorgerSomGrensegjenger")
    LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER_SOM_GRENSEGJENGER("loennKunTrygdeavgiftTilUtenlandskBorgerSomGrensegjenger"),

    @JsonProperty("ufoeretrygdFraNAV")
    UFOERETRYGD_FRA_NAV("ufoeretrygdFraNAV"),

    @JsonProperty("ufoereytelserFraAndre")
    UFOEREYTELSER_FRA_ANDRE("ufoereytelserFraAndre"),

    @JsonProperty("introduksjonsstoenad")
    INTRODUKSJONSSTOENAD("introduksjonsstoenad");

}