package it.polito.wa2.traveler_service.entities

import javax.persistence.*

@Entity
@Table(name = "users")
class UserDetails(
    @Id
    @Column(updatable = false, nullable = false)
    val id: String, //TODO per ora utilizziamo il nickname come chiave primaria, da chidedere

    @Column(nullable = false, unique = true)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var address: String = "",

    @Column(nullable = false, unique = true)
    var date_of_birth: String = "",

    @Column(nullable = false, unique = true)
    var telephon_number: String = "",

    @OneToMany(mappedBy = "userDetails", fetch = FetchType.LAZY)
    val tickets: List<TicketPurchased>? = null

) {
}