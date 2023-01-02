package no.nav.sokos.skattekort.person.database

import java.sql.Connection
import java.sql.ResultSet
import no.nav.sokos.skattekort.person.api.model.SkattekortPerson

fun Connection.hentSkattekortPaaFnrOgInntektsAar(
    skattekortPerson: SkattekortPerson
): String {
    return prepareStatement(
        """
            SELECT NVL2(DATA_MOTTATT, (DATA_MOTTATT).getClobVal(), null)
            FROM OSESKATT_U4.T1_SKATTEKORT_BESTILLING
            WHERE FNR = (?) AND INNTEKTSAAR = (?)
        """.trimIndent()
    ).apply {
        setString(1, skattekortPerson.fnr)
        setString(2, skattekortPerson.inntektsaar)
    }.use { statement ->
        statement.executeQuery().asSequence { statement.resultSet.getString(1) }.first()
    }
}

private fun <T> ResultSet.asSequence(extract: () -> T): Sequence<T> = generateSequence {
    if (this.next()) extract() else null
}