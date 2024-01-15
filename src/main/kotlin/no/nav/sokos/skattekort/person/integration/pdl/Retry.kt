package no.nav.sokos.skattekort.person.integration.pdl

import kotlinx.coroutines.delay

suspend fun <T> retry(
    numOfRetries: Int = 5,
    delayMillis: Long = 250,
    block: suspend () -> T,
): T {
    var throwable: RetryException? = null
    for (n in 1..numOfRetries) {
        try {
            return block()
        } catch (ex: RetryException) {
            throwable = ex
            delay(delayMillis)
        }
    }
    throw throwable!!.exception
}

class RetryException(val exception: Throwable) : Exception()
