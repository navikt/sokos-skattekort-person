package no.nav.sokos.skattekort.person.domain

import java.math.BigDecimal

data class Frikort(
    override val trekkode: Trekkode,
    val frikortbeloep: BigDecimal? = null
) : Forskuddstrekk