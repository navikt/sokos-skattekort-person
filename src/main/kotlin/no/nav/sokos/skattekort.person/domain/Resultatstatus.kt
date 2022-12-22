package no.nav.sokos.skattekort.person.domain

import com.fasterxml.jackson.annotation.JsonProperty

enum class Resultatstatus(val value: String) {
    @JsonProperty("ikkeSkattekort")
    IKKE_SKATTEKORT("ikkeSkattekort"),

    @JsonProperty("vurderArbeidstillatelse")
    VURDER_ARBEIDSTILLATELSE("vurderArbeidstillatelse"),

    @JsonProperty("ikkeTrekkplikt")
    IKKE_TREKKPLIKT("ikkeTrekkplikt"),

    @JsonProperty("skattekortopplysningerOK")
    SKATTEKORTOPPLYSNINGER_OK("skattekortopplysningerOK"),

    @JsonProperty("ugyldigOrganisasjonsnummer")
    UGYLDIG_ORGANISASJONSNUMMER("ugyldigOrganisasjonsnummer"),

    @JsonProperty("ugyldigFoedselsEllerDnummer")
    UGYLDIG_FOEDSELS_ELLER_DNUMMER("ugyldigFoedselsEllerDnummer"),

    @JsonProperty("utgaattDnummerSkattekortForFoedselsnummerErLevert")
    UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT("utgaattDnummerSkattekortForFoedselsnummerErLevert");

}