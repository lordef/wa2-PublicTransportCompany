package it.polito.wa2.payment_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty


data class PaymentInfoDTO(
    @JsonProperty("totalAmount")
    val totalAmount: Float,
    @JsonProperty("creditCardNumber")
    val creditCardNumber: String,
    @JsonProperty("expirationDate")
    val expirationDate: String,
    @JsonProperty("cvv")
    val cvv : String,
    @JsonProperty("cardHolder")
    val cardHolder: String,
    @JsonProperty("username")
    val username: String,
    @JsonProperty("orderId")
    val orderId: Long
)