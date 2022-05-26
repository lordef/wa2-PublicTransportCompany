package it.polito.wa2.traveler_service.dtos

import java.time.ZonedDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PurchaseTicketDTO(
        val cmd: String,

        @field:Min(1)
        val duration: Long,

        @field:NotBlank(message = "Type cannot be empty or null")
        val type: String,

        @field:NotNull
        val validFrom: ZonedDateTime,

        @field:NotNull
        @field:Min(1)
        val quantity: Int,

        @field:NotBlank(message = "Type cannot be empty or null")
        val zone: String
        ) {}


