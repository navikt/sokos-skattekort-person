package no.nav.sokos.skattekort.person.model

data class Arbeidstaker (
    val inntektsaar: Long,
    val arbeidstakeridentifikator: String,
    val resultatPaaForespoersel: Resultatstatus,
    val skattekort: Skattekort,
    val tilleggsopplysning: List<Tilleggsopplysning>? = null
)