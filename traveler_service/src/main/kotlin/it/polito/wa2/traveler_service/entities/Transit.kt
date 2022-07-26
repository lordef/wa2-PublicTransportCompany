package it.polito.wa2.traveler_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("transits")
data class Transit(
    @Id
    private var transitId: Long? = null,

    var timestamp: LocalDateTime,

    val userId: String

)

