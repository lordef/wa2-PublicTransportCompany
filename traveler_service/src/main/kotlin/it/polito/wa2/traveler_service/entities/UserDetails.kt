package it.polito.wa2.traveler_service.entities


import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.Date

@Table("user_details")
data class UserDetails(
    @Id
    val username: String? = null,

    var name: String? = null,

    var address: String? = null,

    var date_of_birth: Date? = null,

    var telephone_number: String? = null,

    /*
        @OneToMany(mappedBy = "userDetails", fetch = FetchType.LAZY)
    val tickets: List<TicketAcquired>? = null,

        @OneToMany(mappedBy = "userDetails", fetch = FetchType.LAZY)
    val transits: List<Transit>? = null*/
)