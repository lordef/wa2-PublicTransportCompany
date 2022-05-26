package it.polito.wa2.traveler_service.dtos

import it.polito.wa2.traveler_service.entities.TicketAcquired


data class TicketAcquiredDTO(
        val sub: Long?,
        var iat: String,
        var nbf: String,//NotBefore, stands for validFrom
        var exp: String,
        var zid: String,
        var type: String,
        var jws: String
) {}

fun TicketAcquired.toDTO(): TicketAcquiredDTO {
    return TicketAcquiredDTO(getId(), issuedAt.toString(), validFrom.toString(), expiry.toString(), zoneId, type,jws)
}
