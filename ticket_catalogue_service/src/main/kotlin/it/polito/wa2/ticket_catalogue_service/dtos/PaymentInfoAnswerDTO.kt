package it.polito.wa2.ticket_catalogue_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import it.polito.wa2.ticket_catalogue_service.entities.Status

data class PaymentInfoAnswerDTO(
    @JsonProperty("status")
    val status: Status,
    @JsonProperty("orderId")
    val orderId: Long
)
