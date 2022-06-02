package it.polito.wa2.ticket_catalogue_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Note that in JPA we would not be able to have a data class, because JPA pretend objects to be extended
 * and replaced dynamically by code. Instead here we have the opportunity to create a data class which is immutable.
 */
@Table("tickets")
data class Ticket(
        @Id
        val ticketId: Long?,
        val price : Float,
        val type : String,
        val minAge: Int?,
        val maxAge: Int?,
)
