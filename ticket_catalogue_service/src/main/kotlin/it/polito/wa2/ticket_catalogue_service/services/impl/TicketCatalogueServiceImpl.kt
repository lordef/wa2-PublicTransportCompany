package it.polito.wa2.ticket_catalogue_service.services.impl


import it.polito.wa2.ticket_catalogue_service.dtos.PaymentInfoDTO
import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.dtos.toDTO
import it.polito.wa2.ticket_catalogue_service.entities.Order
import it.polito.wa2.ticket_catalogue_service.entities.Status
import it.polito.wa2.ticket_catalogue_service.entities.Ticket
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.repositories.OrderRepository
import it.polito.wa2.ticket_catalogue_service.repositories.TicketRepository
import it.polito.wa2.ticket_catalogue_service.security.JwtUtils
import it.polito.wa2.ticket_catalogue_service.services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

import java.time.LocalDate
import java.time.Period
import java.util.*


@Service
@Transactional
class TicketCatalogueServiceImpl(
    @Value("\${kafka.topics.bank_check}") val topic: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>) : TicketCatalogueService {




    val log = LoggerFactory.getLogger(javaClass)



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


    override suspend fun purchaseTickets(principal: String, purchaseTicketsRequestDTO: PurchaseTicketsRequestDTO) : Mono<Long> {

        val ticket = ticketRepository.findById(purchaseTicketsRequestDTO.ticketId)

        if(ticket==null)
            throw BadRequestException("Invalid ticketID")

        //generating jwt for the authentication with Traveler Service
        val jwt = jwtUtils.generateJwt(principal, Date(), Date(Date().time+jwtExpirationMs))

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

            //throw an exception if age constraints are not satisfied
            checkAgeConstraints(userInfo,ticket)
        }

        val totalAmount = (ticket.price*purchaseTicketsRequestDTO.quantity)

        //Save Pending Order
        val order = Order(null,Status.PENDING,purchaseTicketsRequestDTO.ticketId,purchaseTicketsRequestDTO.quantity,totalAmount, principal)
        orderRepository.save(order)



        //Contacting Payment Service
        val request = PaymentInfoDTO(totalAmount,purchaseTicketsRequestDTO.creditCardNumber,purchaseTicketsRequestDTO.expirationDate,purchaseTicketsRequestDTO.cvv,purchaseTicketsRequestDTO.cardHolder, principal,order.orderId as Long)
        contactPaymentService(request)

        return Mono.just(order.orderId as Long)

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

    private fun checkAgeConstraints(userInfo: UserDetailsDTO?, ticket: Ticket){
        if (userInfo == null )
            throw BadRequestException("User Info are not available")

        if(userInfo.date_of_birth == null)
            throw BadRequestException("Date of Birth is not available")

        val date = (userInfo.date_of_birth as String).split("-")
        val userLocalDate = LocalDate.of(date[2].toInt(), date[1].toInt(), date[0].toInt())

        val currentDate = LocalDate.now()

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


    private fun contactPaymentService(request: PaymentInfoDTO ){
        try {
            log.info("Receiving product request")
            log.info("Sending message to Kafka {}", request)
            val message: Message<PaymentInfoDTO> = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
            //ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}",e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
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