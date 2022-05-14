package it.polito.wa2.traveler_service.entities

import java.util.Date
import javax.persistence.*

@Entity
class TicketPurchased(
    @Column(nullable = false, updatable = false)
    var issuedAt: Date,

    @Column(nullable = false, updatable = false)
    var expiry: Date,

    @Column(nullable = false, updatable = false)
    var zoneId: String = "",

    @Column(nullable = false, updatable = true)
    var jws: String = "",

    @ManyToOne(fetch=FetchType.LAZY)
    val userDetails: UserDetails

) : EntityBase<Long>() {}