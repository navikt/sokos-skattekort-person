package no.nav.sokos.skattekort.person.util

object Util {
    fun String.readFromResource() = {}::class.java.classLoader.getResource(this)!!.readText()
}