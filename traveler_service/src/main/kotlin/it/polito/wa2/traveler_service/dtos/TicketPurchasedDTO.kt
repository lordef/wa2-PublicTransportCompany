package it.polito.wa2.traveler_service.dtos

import it.polito.wa2.traveler_service.entities.TicketPurchased


data class TicketPurchasedDTO(
        val sub: Long?,
        var iat: String,
        var exp: String,
        var zid: String,
        var jws: String?
) {}

fun TicketPurchased.toDTO(): TicketPurchasedDTO {
    return TicketPurchasedDTO(getId(), issuedAt.toString(), expiry.toString(), zoneId, jws)
}
