package no.nav.sokos.skattekort.person.pdl

import java.net.URI

import kotlinx.coroutines.runBlocking

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.request.header
import mu.KotlinLogging

import no.nav.sokos.skattekort.person.config.PropertiesConfig
import no.nav.sokos.skattekort.person.config.SECURE_LOGGER
import no.nav.sokos.skattekort.person.config.httpClient
import no.nav.sokos.skattekort.person.pdl.hentperson.Person

private val logger = KotlinLogging.logger {}
private val secureLogger = KotlinLogging.logger(SECURE_LOGGER)

class PdlService(
    private val pdlUrl: String = PropertiesConfig.PdlProperties().pdlUrl,
    private val pdlScope: String = PropertiesConfig.PdlProperties().pdlScope,
    private val graphQlClient: GraphQLKtorClient =
        GraphQLKtorClient(
            URI(pdlUrl).toURL(),
            httpClient,
        ),
    private val accessTokenClient: AccessTokenClient = AccessTokenClient(azureAdScope = pdlScope),
) {
    fun getPersonNavn(ident: String): Person? {
        val result =
            runBlocking {
                val accessToken = accessTokenClient.hentAccessToken()

                graphQlClient.execute(HentPerson(HentPerson.Variables(ident = ident))) {
                    header("Authorization", "Bearer $accessToken")
                }
            }

        return result.errors?.let { errors ->
            if (errors.isEmpty()) {
                hentPerson(result)
            } else {
                handleErrors(errors, ident)
            }
        } ?: hentPerson(result)
    }
}

private fun hentPerson(result: GraphQLClientResponse<HentPerson.Result>): Person? {
    return result.data?.hentPerson
}

private fun handleErrors(
    errors: List<GraphQLClientError>,
    ident: String,
): Person? {
    val errorExtensions = errors.mapNotNull { it.extensions }
    if (errorExtensions.any { it["code"] == "not_found" }) {
        return null
    } else {
        val path = errors.flatMap { it.path ?: emptyList() }
        val errorCode = errorExtensions.map { it["code"] }
        val errorMessage = errorExtensions.map { it["id"] }

        val exceptionMessage =
            "Feil med henting av person fra PDL: (Path: $path, Code: $errorCode, Message: $errorMessage)"
        throw Exception(exceptionMessage).also {
            logger.error("Feil i GraphQL-responsen: (Path: $path, Code: $errorCode, Message: $errorMessage)")
        }.also {
            secureLogger.error("Feil i GraphQL-responsen: (Ident: $ident, Path: $path, Code: $errorCode, Message: $errorMessage)")
        }
    }
}
