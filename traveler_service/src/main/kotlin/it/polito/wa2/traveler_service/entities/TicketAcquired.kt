package it.polito.wa2.traveler_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.Date


@Table("tickets_acquired")
data class TicketAcquired(
    @Id
    val ticketId: Long?,

    var issuedAt: Date,

    var validFrom: Date,

    var expiry: Date,

    var zoneId: String,

    var type: String,

    var jws: String = "",

    val userId: String

)

