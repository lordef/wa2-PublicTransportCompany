package it.polito.wa2.traveler_service.dtos


import it.polito.wa2.traveler_service.services.impl.entities.Ticket
import java.time.LocalDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PurchaseTicketDTO(
        val cmd: String,

        @field:NotNull
        val ticket : Ticket,

        @field:NotNull
        val validFrom: LocalDateTime,

        @field:NotNull
        @field:Min(1)
        val quantity: Long,

        @field:NotBlank(message = "Type cannot be empty or null")
        val zone: String
) {}


