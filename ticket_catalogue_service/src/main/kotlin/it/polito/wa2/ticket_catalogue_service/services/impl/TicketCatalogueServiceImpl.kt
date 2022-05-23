package it.polito.wa2.ticket_catalogue_service.services.impl

import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.dtos.toDTO
import it.polito.wa2.ticket_catalogue_service.repositories.TicketRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.polito.wa2.ticket_catalogue_service.services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Service
@Transactional
class TicketCatalogueServiceImpl() : TicketCatalogueService {

    @Autowired
    lateinit var ticketRepository: TicketRepository

    override fun getAllTickets(): Flow<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

}