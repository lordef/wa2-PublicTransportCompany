package it.polito.wa2.traveler_service.entities

import org.springframework.format.annotation.DateTimeFormat
import java.util.Date
import javax.persistence.*

@Entity
class UserDetails(
        @Id
    @Column(updatable = false, nullable = false, unique = true)
    val username: String? = null, //TODO per ora utilizziamo il nickname come chiave primaria, da chidedere

        @Column(nullable = true, updatable = true)
    var name: String? = null,

        @Column(nullable = true, updatable = true)
    var address: String? = null,

        @Column(nullable = true, updatable = true)
        //@DateTimeFormat(pattern="dd/MM/yyyy")
    var date_of_birth: Date? = null,

        @Column(nullable = true, updatable = true)
    var telephone_number: String? = null,

        @OneToMany(mappedBy = "userDetails", fetch = FetchType.LAZY)
    val tickets: List<TicketPurchased>? = null

)