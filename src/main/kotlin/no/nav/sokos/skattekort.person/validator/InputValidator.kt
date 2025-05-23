package no.nav.sokos.skattekort.person.validator

import java.time.Year

private fun validInput(
    input: String,
    length: Int,
    range: IntRange? = null,
): Boolean {
    val regex = Regex("\\D")
    return !regex.containsMatchIn(input) && input.length == length && (range == null || input.toInt() in range)
}

fun validFnr(fnrInput: String): Boolean {
    return validInput(fnrInput, 11)
}

fun validYear(yearInput: String): Boolean {
    val currentYear = Year.now().value
    return validInput(yearInput, 4, (currentYear - 1)..(currentYear + 1))
}
