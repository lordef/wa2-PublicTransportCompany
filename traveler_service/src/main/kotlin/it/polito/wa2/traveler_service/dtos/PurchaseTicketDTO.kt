package it.polito.wa2.traveler_service.dtos

data class PurchaseTicketDTO(
        val cmd: String,
        val quantity: Int,
        val zone: String
        ) {}


