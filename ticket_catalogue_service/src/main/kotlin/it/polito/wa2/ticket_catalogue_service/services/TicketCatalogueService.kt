package it.polito.wa2.ticket_catalogue_service.services

import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.security.core.annotation.AuthenticationPrincipal

interface TicketCatalogueService {

    fun getAllTickets(): Flow<TicketDTO>
    suspend fun purchaseTickets(principal: String, purchaseTicketsRequestDTO: PurchaseTicketsRequestDTO)

}