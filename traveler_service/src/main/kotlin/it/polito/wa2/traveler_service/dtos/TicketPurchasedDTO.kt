package it.polito.wa2.traveler_service.dtos

import it.polito.wa2.traveler_service.entities.TicketPurchased
import it.polito.wa2.traveler_service.security.JwtUtils
import org.springframework.beans.factory.annotation.Autowired

data class TicketPurchasedDTO(
    //TODO adding contraints

    val ticketId: Long?,
    var issuedAt: String,
    var expiry: String,
    var zoneId: String,
    var jws: String?
) {}

fun TicketPurchased.toDTO( jwtUtils: JwtUtils ): TicketPurchasedDTO {
    return TicketPurchasedDTO(getId(), issuedAt.toString(), expiry.toString(), zoneId, jwtUtils.generateJwt(getId() as Long, issuedAt, expiry, zoneId))
}
