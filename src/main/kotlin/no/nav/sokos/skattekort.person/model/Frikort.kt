package no.nav.sokos.skattekort.person.model

import java.math.BigDecimal

data class Frikort (
    override val trekkode: Trekkode,
    val frikortbeloep: BigDecimal? = null
) : Forskuddstrekk