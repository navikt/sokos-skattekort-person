package no.nav.sokos.skattekort.person.model

data class Arbeidsgiver (
    val arbeidstaker: List<Arbeidstaker>,
    val arbeidsgiveridentifikator: IdentifikatorForEnhetEllerPerson
)