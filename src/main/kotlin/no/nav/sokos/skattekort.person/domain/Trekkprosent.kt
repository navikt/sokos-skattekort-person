package no.nav.sokos.skattekort.person.domain

import java.math.BigDecimal

data class Trekkprosent(
    override val trekkode: Trekkode,
    val prosentsats: BigDecimal? = null,
    var antallMaanederForTrekk: BigDecimal? = null,
) : Forskuddstrekk
