package no.nav.sokos.skattekort.person.domain

data class Arbeidsgiver(
    val arbeidstaker: List<Arbeidstaker>,
    val arbeidsgiveridentifikator: IdentifikatorForEnhetEllerPerson
)