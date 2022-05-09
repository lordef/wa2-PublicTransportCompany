package it.polito.wa2.traveler_service.entities

import javax.persistence.*

@Entity
@Table(name = "users")
class UserDetails(
    @Id
    @Column(updatable = false, nullable = false)
    val username: String? = null, //TODO per ora utilizziamo il nickname come chiave primaria, da chidedere

    @Column(nullable = false, unique = true)
    var name: String? = null,

    @Column(nullable = false, unique = true)
    var address: String? = null,

    @Column(nullable = false, unique = true)
    var date_of_birth: String? = null,

    @Column(nullable = false, unique = true)
    var telephone_number: String? = null,

    @OneToMany(mappedBy = "userDetails", fetch = FetchType.LAZY)
    val tickets: List<TicketPurchased>? = null

)