package it.polito.wa2.traveler_service.repositories

import it.polito.wa2.traveler_service.entities.TicketPurchased
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketPurchasedRepository : CrudRepository<TicketPurchased, Long> {


}