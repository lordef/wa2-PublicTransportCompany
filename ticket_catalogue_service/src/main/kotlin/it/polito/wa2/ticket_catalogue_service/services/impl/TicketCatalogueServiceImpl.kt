package it.polito.wa2.ticket_catalogue_service.services.impl

import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.dtos.toDTO
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.repositories.TicketRepository
import it.polito.wa2.ticket_catalogue_service.services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.lang.System.err


@Service
@Transactional
class TicketCatalogueServiceImpl() : TicketCatalogueService {

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    private val webClient = WebClient.create(("http://localhost:8080"))

    override fun getAllTickets(): Flow<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    override suspend fun purchaseTickets(purchaseTicketsRequestDTO: PurchaseTicketsRequestDTO) {

        val ticket = ticketRepository.findById(purchaseTicketsRequestDTO.ticketId)

        if(ticket==null)
            throw BadRequestException("Invalid ticketID")


        //NOTA: è solo per provare, ma qui ci va messo l'id dello user ricavato dall'autenticazione del jwt
        val userInfo = webClient.get().uri("/admin/traveler/1/profile")
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjIwIiwiaWF0IjoxNjUyODkxODc5LCJleHAiOjE3MTYwNTEyNjIsInJvbGVzI" +
                        "jpbIkNVU1RPTUVSIiwiQURNSU4iXX0.cJ9OjS-ojAz46YUbWQw5vrj4mQ_QQ1kv7UAuG3JZ7e5tahtm0z39ruFfnmKUmUmHn4d11NrHPYso0AKsuPPY_Q")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono { response ->

                    if (response.statusCode() == HttpStatus.OK) {
                        response.bodyToMono(UserDetailsDTO::class.java)
                    } else {
                        println(response.statusCode())
                        throw BadRequestException("User Info are not available")
                    }
                }.subscribe({ //subscribe serve per "registrarsi" al Mono, e, appena disponibile, esegurire la callback
                    it -> println(it)

                    //TODO qui va implementato la verifica sull'età
                    //TODO va chiesto al professore come facciamo a sapere se un certo biglietto è ad esempio under 27? Va scritto nel codice con uno switch sull'età? O l'info sull'età massima è scritto nel DB?
                })

    }


}

data class UserDetailsDTO(
        var username: String,
        var name: String,
        var address: String,
        var telephone_number: String,
        var date_of_birth: String
)