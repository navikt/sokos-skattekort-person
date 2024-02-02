package no.nav.sokos.skattekort.person.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.ktor.http.HttpStatusCode
import java.net.URI
import no.nav.sokos.skattekort.person.config.PropertiesConfig
import no.nav.sokos.skattekort.person.setupMockEngine
import org.junit.jupiter.api.assertThrows

private const val pdlUrl = "http://0.0.0.0"

internal class PdlServiceTest : FunSpec({

    test("Fant person i PDL") {
        val result = PdlService(
            pdlConfig = PropertiesConfig.PdlConfig(),
            GraphQLKtorClient(
                URI(pdlUrl).toURL(),
                setupMockEngine(
                    "pdl/hentPerson_fant_person_response.json",
                    HttpStatusCode.OK
                )
            ),
            accessTokenClient = null
        ).getPersonNavn("22334455667")

        result shouldNot beNull()
        result?.navn?.first()?.fornavn shouldBe "TRIVIELL"
        result?.navn?.first()?.mellomnavn shouldBe beNull()
        result?.navn?.first()?.etternavn shouldBe "SKILPADDE"
    }


    test("Fant ikke person i PDL") {
        val result = PdlService(
            pdlConfig = PropertiesConfig.PdlConfig(),
            GraphQLKtorClient(
                URI(pdlUrl).toURL(),
                setupMockEngine(
                    "pdl/hentPerson_fant_ikke_person_response.json",
                    HttpStatusCode.OK
                )
            ),
            accessTokenClient = null
        ).getPersonNavn("22334455667")

        result shouldBe beNull()
    }

    test("Ikke authentisert mot PDL") {
        val exception = assertThrows<Exception> {
            PdlService(
                pdlConfig = PropertiesConfig.PdlConfig(),
                GraphQLKtorClient(
                    URI(pdlUrl).toURL(),
                    setupMockEngine(
                        "pdl/hentPerson_ikke_authentisert_response.json"
                    )
                ),
                accessTokenClient = null
            ).getPersonNavn("22334455667")
        }

        exception shouldNotBe beNull()
        exception.message shouldBe "Feil med henting av person fra PDL: (Path: [hentPerson], Code: [unauthenticated], Message: [null])"
    }

})