package it.polito.wa2.ticket_catalogue_service.dtos

import it.polito.wa2.ticket_catalogue_service.entities.Ticket
import org.springframework.beans.factory.annotation.Value
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class TicketDTO(
    @field:NotNull
    @field:Min(1)
    val price : Float,

    val ticketID : Long?,

    @field:NotBlank
    val type: String,//it will be ordinal or seasonal


    @field:NotBlank
    val name : String,

    @field:Min(0)
    val minAge: Int?,

    @field:Min(1)
    val maxAge: Int?,


    @field:Min(1)
    @field:NotNull
    val duration: Int?
)

fun Ticket.toDTO() : TicketDTO {
    return TicketDTO(price,ticketId,type, name, minAge, maxAge, duration)
}
