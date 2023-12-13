package no.nav.sokos.skattekort.person.util

import java.time.Year

fun validInput(numberInput: String): Boolean {
    return numberInput.isEmpty() || numberInput.all { it.isDigit() }
}

fun validYear(year: String): Boolean {
    val currentYear = Year.now().value
    return year.toInt() < currentYear - 1 || year.toInt() > currentYear + 1
}

fun validFnr(fnr: String): Boolean {
    return fnr.length == 11
}