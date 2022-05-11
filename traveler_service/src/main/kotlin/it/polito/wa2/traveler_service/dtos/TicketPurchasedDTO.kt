package it.polito.wa2.traveler_service.dtos

import it.polito.wa2.traveler_service.entities.TicketPurchased
import it.polito.wa2.traveler_service.security.JwtUtils

data class TicketPurchasedDTO(
    //TODO adding contraints

        val sub: Long?,
        var iat: String,
        var exp: String,
        var zid: String,
        var jws: String?
) {}

fun TicketPurchased.toDTO( jwtUtils: JwtUtils ): TicketPurchasedDTO {
    return TicketPurchasedDTO(getId(), issuedAt.toString(), expiry.toString(), zoneId, jwtUtils.generateJwt(getId() as Long, issuedAt, expiry, zoneId))
}
