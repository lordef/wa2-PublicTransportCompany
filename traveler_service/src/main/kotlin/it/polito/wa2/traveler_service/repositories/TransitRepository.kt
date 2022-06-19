package it.polito.wa2.traveler_service.repositories

import it.polito.wa2.traveler_service.entities.Transit
import it.polito.wa2.traveler_service.entities.UserDetails
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TransitRepository : CrudRepository<Transit, Long> {
}