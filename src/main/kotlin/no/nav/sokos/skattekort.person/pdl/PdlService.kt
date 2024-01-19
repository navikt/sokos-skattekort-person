package no.nav.sokos.skattekort.person.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import java.net.URI
import kotlinx.coroutines.runBlocking
import no.nav.sokos.skattekort.person.config.PropertiesConfig
import no.nav.sokos.skattekort.person.pdl.hentperson.Person
import no.nav.sokos.skattekort.person.util.defaultHttpClient

class PdlService(
    private val pdlConfig: PropertiesConfig.PdlConfig = PropertiesConfig.PdlConfig(),
    private val graphQLKtorClient: GraphQLKtorClient = GraphQLKtorClient(
        URI(pdlConfig.pdlHost).toURL(),
        defaultHttpClient
    ),
    private val accessTokenClient: AccessTokenClient = AccessTokenClient(),
) {

    fun getPersonNavn(ident: String): Person? {

        val request = HentPerson(HentPerson.Variables(ident = ident))

        val result = runBlocking {
            val accessToken = accessTokenClient.getSystemToken()

            graphQLKtorClient.execute(request) {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                header("Tema", "OKO")
            }
        }

        return result.errors?.let { errors ->
            if (errors.isEmpty()) {
                hentPerson(result)
            } else {
                handleErrors(errors)
            }
        } ?: hentPerson(result)
    }

}

private fun hentPerson(result: GraphQLClientResponse<HentPerson.Result>): Person? {
    return result.data?.hentPerson
}

private fun handleErrors(errors: List<GraphQLClientError>): Person? {
    val errorExtensions = errors.mapNotNull { it.extensions }
    if (errorExtensions.any { it["code"] == "not_found" }) {
        return null
    } else {
        val path = errors.flatMap { it.path ?: emptyList() }
        val errorCode = errorExtensions.map { it["code"] }
        val errorMessage = errorExtensions.map { it["id"] }

        val exceptionMessage = "Feil med henting av person fra PDL: (Path: $path, Code: $errorCode, Message: $errorMessage)"
        throw Exception(exceptionMessage)
    }
}
