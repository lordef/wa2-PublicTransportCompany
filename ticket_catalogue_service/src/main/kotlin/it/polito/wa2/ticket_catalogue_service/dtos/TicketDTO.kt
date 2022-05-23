package it.polito.wa2.ticket_catalogue_service.dtos

import it.polito.wa2.ticket_catalogue_service.entities.Ticket

data class TicketDTO(
        val price : Float,
        val ticketID : Long,
        val type : String
)

fun Ticket.toDTO() : TicketDTO {
    return TicketDTO(price,ticketId,type)
}
