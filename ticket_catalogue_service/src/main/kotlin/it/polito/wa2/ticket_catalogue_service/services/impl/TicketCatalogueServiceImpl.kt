package it.polito.wa2.ticket_catalogue_service.services.impl


import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.dtos.toDTO
import it.polito.wa2.ticket_catalogue_service.entities.Order
import it.polito.wa2.ticket_catalogue_service.entities.Status
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.repositories.OrderRepository
import it.polito.wa2.ticket_catalogue_service.repositories.TicketRepository
import it.polito.wa2.ticket_catalogue_service.security.JwtUtils
import it.polito.wa2.ticket_catalogue_service.services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import java.time.Period
import java.util.*


@Service
@Transactional
class TicketCatalogueServiceImpl() : TicketCatalogueService {

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

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

        //generating jwt for the authentication with Traveler Service and Payment Service
        val jwt = jwtUtils.generateJwt(principal, Date(), Date(Date().time+jwtExpirationMs))

        orderRepository.save(Order(null,Status.PENDING,1,3,2.84, principal))

        //se è necessario controllare l'età...
        if(ticket.maxAge!= null || ticket.minAge!=null) {

            //TODO si potrebbe mettere questo pezzo di codice in una funzione a cui passiamo solo il web client
            val userInfo = webClient.get().uri("/admin/traveler/${principal}/profile")
                .header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono { response ->

                    if (response.statusCode() == HttpStatus.OK) {
                        response.bodyToMono(UserDetailsDTO::class.java)
                    } else {
                        println(response.statusCode())
                        throw BadRequestException("User Info are not available")
                    }
                }.awaitSingleOrNull()

            if (userInfo == null )
                throw BadRequestException("User Info are not available")

            if(userInfo.date_of_birth == null)
                throw BadRequestException("Date of Birth is not available")

            val date = (userInfo.date_of_birth as String).split("-")
            val userLocalDate = LocalDate.of(date[2].toInt(), date[1].toInt(), date[0].toInt())

            val currentDate = LocalDate.of(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)

            //TODO da testare
            val currentNumberOfYears = calculateAge(userLocalDate, currentDate)

            if (currentNumberOfYears == 0)
                throw BadRequestException("Invalid Age for this ticket type")

            if (ticket.maxAge!=null)
                if(currentNumberOfYears>ticket.maxAge)
                    throw BadRequestException("Invalid Age for this ticket type")

            if (ticket.minAge!=null)
                if(currentNumberOfYears<ticket.minAge)
                    throw BadRequestException("Invalid Age for this ticket type")

        }

        //TODO contattare il Payment Service



            /*.subscribe({ //subscribe serve per "registrarsi" al Mono, e, appena disponibile, esegurire la callback
                    it -> println(it)

                    //suppongo vada fatto qui dentro il tutto : cioè solo una volta ricevuta risposta, procedo a fare il resto

                    //TODO qui va implementato la verifica sull'età

                    //TODO qui va poi verificata la disponibilità economica invocando il payment service
                })*/

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

    fun calculateAge(birthDate: LocalDate?, currentDate: LocalDate?): Int {
        return if (birthDate != null && currentDate != null) {
            Period.between(birthDate, currentDate).getYears()
        } else {
            0
        }
    }


}

data class UserDetailsDTO(
        var username: String,
        var name: String?,
        var address: String?,
        var telephone_number: String?,
        var date_of_birth: String?
)