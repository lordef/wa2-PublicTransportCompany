package it.polito.wa2.ticket_catalogue_service.controllers

import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.springframework.web.bind.annotation.RestController

@RestController
class TicketCatalogueController {

    fun getTickets() : Flow<TicketDTO> {
        return flowOf()
    }

}