package it.polito.wa2.traveler_service.repositories

import it.polito.wa2.traveler_service.entities.TicketAcquired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TicketPurchasedRepository : CrudRepository<TicketAcquired, Long> {

    @Transactional(readOnly = true)
    fun findAllByUserDetailsUsername(userDetailsUsername : String) : List<TicketAcquired>
}