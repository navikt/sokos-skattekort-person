package no.nav.sokos.skattekort.person.domain

import java.math.BigDecimal

data class Trekktabell(
    override val trekkode: Trekkode,
    val tabelltype: Tabelltype? = null,
    val tabellnummer: String? = null,
    val prosentsats: BigDecimal? = null,
    val antallMaanederForTrekk: BigDecimal? = null,
) : Forskuddstrekk
