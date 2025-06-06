package no.nav.sokos.skattekort.person.security

import com.auth0.jwt.JWT
import io.ktor.server.application.ApplicationCall

import no.nav.sokos.skattekort.person.auditlogg.Saksbehandler

private const val JWT_CLAIM_NAVIDENT = "NAVident"

fun getSaksbehandler(call: ApplicationCall): Saksbehandler {
    val oboToken =
        call.request.headers["Authorization"]?.removePrefix("Bearer ")
            ?: throw IllegalStateException("Could not get token from request header")
    val navIdent = getNAVIdentFromToken(oboToken)

    return Saksbehandler(navIdent)
}

private fun getNAVIdentFromToken(token: String): String {
    val decodedJWT = JWT.decode(token)
    return decodedJWT.claims[JWT_CLAIM_NAVIDENT]?.asString()
        ?: throw RuntimeException("Missing NAVident in private claims")
}
