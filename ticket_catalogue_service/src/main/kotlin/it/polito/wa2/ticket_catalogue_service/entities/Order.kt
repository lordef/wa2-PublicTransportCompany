package it.polito.wa2.ticket_catalogue_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
    @Id
    var orderId: Long?=null,
    var status: Status = Status.PENDING,
    val ticketType: Long, //references Tickets table
    val notBefore: String,
    val quantity: Long,
    val totalPrice: Float,
    val userId: String,
    val zoneId: String,
    val ticket_generated: Boolean

)