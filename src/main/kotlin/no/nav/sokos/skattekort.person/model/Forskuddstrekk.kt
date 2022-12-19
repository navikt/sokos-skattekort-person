package no.nav.sokos.skattekort.person.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Frikort::class, name = "Frikort"),
    JsonSubTypes.Type(value = Trekktabell::class, name = "Trekktabell"),
    JsonSubTypes.Type(value = Trekkprosent::class, name = "Trekkprosent")
)
interface Forskuddstrekk {
    val trekkode: Trekkode
}
