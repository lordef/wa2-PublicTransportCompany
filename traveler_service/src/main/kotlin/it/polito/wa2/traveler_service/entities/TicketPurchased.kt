package it.polito.wa2.traveler_service.entities

import javax.persistence.*

@Entity
class TicketPurchased(
    @Column(nullable = false)
    var issuedAt: String = "",

    @Column(nullable = false)
    var expiry: String = "",

    @Column(nullable = false)
    var zoneId: String = "",

    @Column(nullable = false)
    var sign: String = "",

    @ManyToOne()
    val userDetails: UserDetails

) : EntityBase<Long>() {}