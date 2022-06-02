package it.polito.wa2.ticket_catalogue_service.dtos

import it.polito.wa2.ticket_catalogue_service.entities.Ticket
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class TicketDTO(
    @field:NotNull
    val price : Float,

    val ticketID : Long?,

    @field:NotBlank
    val type : String,

    @field:Min(0)
    val minAge: Int?,

    @field:Min(1)
    val maxAge: Int?
)

fun Ticket.toDTO() : TicketDTO {
    return TicketDTO(price,ticketId,type, minAge, maxAge)
}
