package no.nav.sokos.skattekort.person.integration.pdl

import io.ktor.client.statement.HttpResponse
import no.nav.sokos.skattekort.person.util.ApiError

class PdlException(val apiError: ApiError, val response: HttpResponse) : Exception(apiError.error)
