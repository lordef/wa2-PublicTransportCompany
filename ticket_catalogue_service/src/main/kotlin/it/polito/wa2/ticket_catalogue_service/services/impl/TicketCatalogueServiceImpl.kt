package it.polito.wa2.ticket_catalogue_service.services.impl

import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.time
import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.dtos.toDTO
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.repositories.TicketRepository
import it.polito.wa2.ticket_catalogue_service.security.JwtUtils
import it.polito.wa2.ticket_catalogue_service.services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.lang.System.err
import java.util.*


@Service
@Transactional
class TicketCatalogueServiceImpl() : TicketCatalogueService {

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Value("\${application.jwt.jwtExpirationMs}")
    var jwtExpirationMs: Long = -1

    private val webClient = WebClient.create(("http://localhost:8080"))

    private val jwtUtils = JwtUtils()

    override fun getAllTickets(): Flow<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    override suspend fun purchaseTickets(principal: String, purchaseTicketsRequestDTO: PurchaseTicketsRequestDTO) {

        val ticket = ticketRepository.findById(purchaseTicketsRequestDTO.ticketId)

        if(ticket==null)
            throw BadRequestException("Invalid ticketID")

        //generating jwt for the authentication with Traveler Service
        val jwt = jwtUtils.generateJwt(principal, Date(), Date(Date().time+jwtExpirationMs))


        val userInfo = webClient.get().uri("/admin/traveler/${principal}/profile")
                .header("Authorization", "Bearer "+jwt)
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

                    //suppongo vada fatto qui dentro il tutto : cioè solo una volta ricevuta risposta, procedo a fare il resto

                    //TODO qui va implementato la verifica sull'età

                    //TODO qui va poi verificata la disponibilità economica invocando il payment service
                })

    }

    //TODO l'idea è creare una funzione generica per get e post, che ritorni un Publisher a cui basta fare subscribe
    //TODO in tal modo codice più leggibile e pulito, altrimenti esce una cosa abnorme
    /*fun <T> doGetReactive(uri: String, className: Class<T>, single: Boolean = false): Publisher<T> {
        val returnValue: Publisher<T>

        try {
            returnValue = if (single)
                webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(className)
            else
                webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(className)
        }
        catch (e: Exception){
            return Mono.error(ServiceUnavailable("Error during connection with other server"))
        }


        return if (returnValue is Mono)
            returnValue.switchIfEmpty (
                Mono.error(ServiceUnavailable("No response from other server")))
        else
            returnValue
    }*/


}

data class UserDetailsDTO(
        var username: String,
        var name: String,
        var address: String,
        var telephone_number: String,
        var date_of_birth: String
)