package it.polito.wa2.ticket_catalogue_service.dtos

data class TicketDTO(
        val price : Float,
        val ticketID : Long,
        val type : String
)
