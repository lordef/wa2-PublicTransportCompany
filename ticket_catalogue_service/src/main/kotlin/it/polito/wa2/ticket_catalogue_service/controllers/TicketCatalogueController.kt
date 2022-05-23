package it.polito.wa2.ticket_catalogue_service.controllers

import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.services.impl.TicketCatalogueServiceImpl
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class TicketCatalogueController {

    @Autowired
    lateinit var catalogueService: TicketCatalogueServiceImpl

    @GetMapping("/tickets")
    fun getTickets() : Flow<TicketDTO> {
        return catalogueService.getAllTickets()
    }

    /*@PostMapping("/shop/{ticketId}")
    suspend fun addProduct(/*@RequestBody @Valid product: ProductDTO): ProductDTO*/{
        return
    }*/

}