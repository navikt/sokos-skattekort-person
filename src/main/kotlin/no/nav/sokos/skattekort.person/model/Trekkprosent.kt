package no.nav.sokos.skattekort.person.model

import java.math.BigDecimal

data class Trekkprosent(
    override val trekkode: Trekkode,
    val prosentsats: BigDecimal? = null,
    var antallMaanederForTrekk: BigDecimal? = null
) : Forskuddstrekk