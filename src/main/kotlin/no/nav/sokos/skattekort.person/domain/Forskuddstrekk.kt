package no.nav.sokos.skattekort.person.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    // Type blir med i JSON responsen som forteller hvilken type Forskuddstrekk dette er
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Frikort::class, name = "Frikort"),
    JsonSubTypes.Type(value = Trekktabell::class, name = "Trekktabell"),
    JsonSubTypes.Type(value = Trekkprosent::class, name = "Trekkprosent"),
)
interface Forskuddstrekk {
    val trekkode: Trekkode
}
