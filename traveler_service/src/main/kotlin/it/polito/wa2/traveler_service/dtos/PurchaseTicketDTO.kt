package it.polito.wa2.traveler_service.dtos


import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PurchaseTicketDTO(
        val cmd: String,

        @field:NotBlank(message = "Type cannot be empty or null")
        val type: String,

        @field:NotBlank(message = "Name cannot be empty or null")
        val name: String,

        @field:NotNull
        val validFrom: String,

        @field:NotNull
        @field:Min(1)
        val quantity: Long,

        @field:Min(1)
        val duration: Long?,

        @field:NotBlank(message = "Type cannot be empty or null")
        val zone: String
) {}


