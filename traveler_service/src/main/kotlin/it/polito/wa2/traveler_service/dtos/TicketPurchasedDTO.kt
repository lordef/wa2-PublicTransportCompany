package it.polito.wa2.traveler_service.dtos

import it.polito.wa2.traveler_service.entities.TicketPurchased


data class TicketPurchasedDTO(
    //TODO adding contraints

    val ticketId: Long?,
    var issuedAt: String,
    var expiry: String,
    var zoneId: String,
    var sign: String
) {}

fun TicketPurchased.toDTO(): TicketPurchasedDTO {
    return TicketPurchasedDTO(getId(), issuedAt, expiry, zoneId, sign)
}
