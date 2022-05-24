package it.polito.wa2.ticket_catalogue_service.dtos

import javax.validation.constraints.Min
import javax.validation.constraints.Size
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class PurchaseTicketsRequestDTO(
        @field:NotNull
        @field:Min(1)
        val quantity: Long,

        @field:NotNull
        @field:Min(1)
        val ticketId: Long,

        @field:NotNull
        @field:Size(min=13,max = 16, message = "credit card number has wrong number of digits")
        val creditCardNumber: String,

        //contraints for date
        @field:Pattern(regexp = "([0-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-[0-9]{4}")
        @field:NotNull
        val expirationDate: String,

        @field:NotNull
        @field:Size(min=3,max = 3, message = "cvv has wrong number of digits")
        val cvv : String,

        @field:NotNull
        val cardHolder: String
)
