package it.polito.wa2.ticket_catalogue_service.dtos

import it.polito.wa2.ticket_catalogue_service.entities.Ticket
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class TicketDTO(
    @field:NotNull
    val price : Float,

    @field:NotNull
    val ticketID : Long?,

    @field:NotBlank
    val type : String,

    val minAge: Int?,
    val maxAge: Int?,

    @field:NotNull
    val duration: Long? //validity of ticket expressed in minutes
)

fun Ticket.toDTO() : TicketDTO {
    return TicketDTO(price,ticketId,type, minAge, maxAge, duration)
}
