package no.nav.sokos.skattekort.person.domain

import java.time.LocalDate

data class Skattekort(
    val inntektsaar: Long,
    val utstedtDato: LocalDate,
    val skattekortidentifikator: Long,
    val forskuddstrekk: List<Forskuddstrekk>? = null
)