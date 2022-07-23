package it.polito.wa2.ticket_catalogue_service.services.impl


import it.polito.wa2.ticket_catalogue_service.dtos.*
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
import java.text.SimpleDateFormat

import java.time.LocalDate
import java.time.Period
import java.util.*


@Service
@Transactional
class TicketCatalogueServiceImpl(
    @Value("\${kafka.topics.bank_check}") val topic: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : TicketCatalogueService {


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


    override suspend fun purchaseTickets(
        principal: String,
        purchaseTicketsRequestDTO: PurchaseTicketsRequestDTO
    ): Mono<Long> {

        val ticket = ticketRepository.findById(purchaseTicketsRequestDTO.ticketId)

        if (ticket == null)
            throw BadRequestException("Invalid ticketID")


        checkValidityOfValidFrom(ticket.type, ticket.name, purchaseTicketsRequestDTO.notBefore)
        if(ticket.type=="seasonal" && (ticket.duration==null || ticket.duration<1))
            throw BadRequestException("Invalid duration")

        //generating jwt for the authentication with Traveler Service
        val jwt = jwtUtils.generateJwt(principal, Date(), Date(Date().time + jwtExpirationMs))


        val userInfo = webClient.get().uri("/admin/traveler/${principal}/profile")
            .header("Authorization", "Bearer " + jwt)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono { response ->

                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToMono(UserDetailsDTO::class.java)
                } else {
                    println(response.statusCode())
                    throw BadRequestException("User is not present in User Details")
                }
            }.awaitSingleOrNull()


        //se è necessario controllare l'età...
        if (ticket.maxAge != null || ticket.minAge != null) {
            //throw an exception if age constraints are not satisfied
            checkAgeConstraints(userInfo, ticket)
        }

        val totalAmount = (ticket.price * purchaseTicketsRequestDTO.quantity)

        //Save Pending Order
        val order = Order(
            null,
            Status.PENDING,
            purchaseTicketsRequestDTO.ticketId,
            purchaseTicketsRequestDTO.notBefore,
            purchaseTicketsRequestDTO.quantity,
            totalAmount,
            principal,
            purchaseTicketsRequestDTO.zoneId
        )
        orderRepository.save(order)


        //Contacting Payment Service
        val request = PaymentInfoDTO(
            totalAmount,
            purchaseTicketsRequestDTO.creditCardNumber,
            purchaseTicketsRequestDTO.expirationDate,
            purchaseTicketsRequestDTO.cvv,
            purchaseTicketsRequestDTO.cardHolder,
            principal,
            order.orderId as Long
        )
        contactPaymentService(request)

        return Mono.just(order.orderId as Long)

    }

    override fun getOrdersByUserId(userId: String): Flow<OrderDTO> {
        return orderRepository.findByUserId(userId).map { it.toDTO() }
    }

    override suspend fun getOrderByOrderIdAndUserId(userId: String, orderId: Long): Mono<OrderDTO> {
        return orderRepository.findOrderByOrderIdAndUserId(orderId, userId).map {
            if (it == null)
                null
            else it.toDTO()
        }
    }

    override fun getAllOrdersByAllUsers(): Flow<OrderDTO> {
        return orderRepository.findAll().map { it.toDTO() }
    }

    override suspend fun addTicket(ticketDTO: TicketDTO) {
        val ticket = ticketRepository.findByName(ticketDTO.name)

        if (ticket != null)
            throw BadRequestException("Invalid ticket name")

        val ticketEntity = Ticket(
            null,
            ticketDTO.price,
            ticketDTO.type,
            ticketDTO.name,
            ticketDTO.minAge,
            ticketDTO.maxAge,
            ticketDTO.duration
        )
        ticketRepository.save(ticketEntity)
    }

    //TODO
    override suspend fun updateTicket(ticketDTO: TicketDTO) {
        val ticket = ticketRepository.findById(ticketDTO.ticketID!!)

        if (ticket != null)
            throw BadRequestException("Invalid ticket id")

        /*
        if ordinal -> modify only price
        if seasonal -> update (add/remove active columns: min_age, max_age, start_period, end_period)
                        and modify some value (price, name, min_age, max_age, start_period, end_period, duration)
        */

        val ticketEntity = Ticket(
            null,
            ticketDTO.price,
            ticketDTO.type,
            ticketDTO.name,
            ticketDTO.minAge,
            ticketDTO.maxAge,
            ticketDTO.duration
        )
//        ticketRepository.save(ticketEntity)

        //It does not update. but delete the row and recreate a new one
        ticketRepository.deleteById(ticket?.ticketId!!)
        ticketRepository.save(ticketEntity)
    }



    //

    fun calculateAge(birthDate: LocalDate?, currentDate: LocalDate?): Int {
        return if (birthDate != null && currentDate != null) {
            Period.between(birthDate, currentDate).getYears()
        } else {
            0
        }
    }

    private fun checkAgeConstraints(userInfo: UserDetailsDTO?, ticket: Ticket) {


        if (userInfo!!.date_of_birth == null)
            throw BadRequestException("Date of Birth is not available")

        val date = (userInfo.date_of_birth as String).split("-")
        val userLocalDate = LocalDate.of(date[2].toInt(), date[1].toInt(), date[0].toInt())

        val currentDate = LocalDate.now()

        val currentNumberOfYears = calculateAge(userLocalDate, currentDate)

        if (currentNumberOfYears == 0)
            throw BadRequestException("Invalid Age for this ticket type")

        if (ticket.maxAge != null)
            if (currentNumberOfYears > ticket.maxAge)
                throw BadRequestException("Invalid Age for this ticket type")

        if (ticket.minAge != null)
            if (currentNumberOfYears < ticket.minAge)
                throw BadRequestException("Invalid Age for this ticket type")
    }


    private fun contactPaymentService(request: PaymentInfoDTO) {
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
            log.error("Exception: {}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }


    private fun checkValidityOfValidFrom(type: String, name: String, validFrom: String) {

        val formatter = SimpleDateFormat("dd-MM-yyyy")

        if (type == "ordinal") {

            when (name) {
                "70 minutes" -> {

                }
                "daily" -> {

                }
                "weekly" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is a Monday
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                        throw BadRequestException("Invalid ValidFrom field")

                }
                "monthly" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is the first of Any Month
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    if (cal.get(Calendar.DAY_OF_MONTH) != cal.getActualMinimum(Calendar.DAY_OF_MONTH))
                        throw BadRequestException("Invalid ValidFrom field")
                }
                "biannually" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is the first of Any Month
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    if (cal.get(Calendar.DAY_OF_MONTH) != cal.getActualMinimum(Calendar.DAY_OF_MONTH))
                        throw BadRequestException("Invalid ValidFrom field")

                }
                "yearly" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is the first of Any Month
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    if (cal.get(Calendar.DAY_OF_MONTH) != cal.getActualMinimum(Calendar.DAY_OF_MONTH))
                        throw BadRequestException("Invalid ValidFrom field")

                }
                "weekend_pass" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is a Saturday or a Sunday
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY)
                        throw BadRequestException("Invalid ValidFrom field")

                }
                else -> {
                    throw BadRequestException("Invalid ValidFrom field")
                }
            }

        }else{

            val date = formatter.format(Date())
            if(date!=validFrom)
                throw  BadRequestException("NotBefore must be current date for seasonal types")
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