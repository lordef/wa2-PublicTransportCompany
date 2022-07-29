package it.polito.wa2.ticket_catalogue_service.services.impl


import it.polito.wa2.ticket_catalogue_service.dtos.*
import it.polito.wa2.ticket_catalogue_service.entities.Order
import it.polito.wa2.ticket_catalogue_service.entities.PaymentInfo
import it.polito.wa2.ticket_catalogue_service.entities.Status
import it.polito.wa2.ticket_catalogue_service.entities.Ticket
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.repositories.OrderRepository
import it.polito.wa2.ticket_catalogue_service.repositories.TemporaryPaymentRepository
import it.polito.wa2.ticket_catalogue_service.repositories.TicketRepository
import it.polito.wa2.ticket_catalogue_service.security.JwtUtils
import it.polito.wa2.ticket_catalogue_service.services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
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
import java.time.format.DateTimeFormatter
import java.util.*


@Service
@Transactional
class TicketCatalogueServiceImpl(
    //@Value("\${kafka.topics.bank_check}") val topicPayment: String,
    @Value("\${kafka.topics.customer_check}") val topicTraveler: String,

    @Autowired
    private val kafkaTemplateTraveler: KafkaTemplate<String, Any>
) : TicketCatalogueService {


    val log = LoggerFactory.getLogger(javaClass)


    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var paymentRepository: TemporaryPaymentRepository

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


        checkValidityOfValidFrom(ticket.type, ticket.name, purchaseTicketsRequestDTO.notBefore.toLocalDate().toString(), ticket)
        if (ticket.type == "seasonal" && (ticket.duration == null || ticket.duration < 1))
            throw BadRequestException("Invalid duration")

        /*
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
            }.awaitSingleOrNull()*/


        val totalAmount = (ticket.price * purchaseTicketsRequestDTO.quantity)

        //Save Pending Order
        val order = Order(
            null,
            Status.CREATED,
            purchaseTicketsRequestDTO.ticketId,
            purchaseTicketsRequestDTO.notBefore,
            purchaseTicketsRequestDTO.quantity,
            totalAmount,
            principal,
            purchaseTicketsRequestDTO.zoneId
        )
        orderRepository.save(order)

        val paymentInfo = PaymentInfo(order.orderId!!,purchaseTicketsRequestDTO.creditCardNumber,purchaseTicketsRequestDTO.expirationDate,purchaseTicketsRequestDTO.cvv,purchaseTicketsRequestDTO.cardHolder)
        paymentRepository.save(paymentInfo)

        val username = UsernameDTO(principal,order.orderId!!);
        contactTravelerService(username)

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
            ticketDTO.start_period,
            ticketDTO.end_period,
            ticketDTO.duration
        )
        ticketRepository.save(ticketEntity)
    }

    override suspend fun updateTicket(ticketDTO: TicketDTO) {
        val ticket = ticketRepository.findById(ticketDTO.ticketID!!)

        if (ticket == null)
            throw BadRequestException("Invalid ticket id")

        val ticketEntity: Ticket =
            if (ticketDTO.type != "ordinal") {
                // allowed modifications: price, min_age, max_age //TODO: allow also name?
                Ticket(
                    ticket.ticketId,
                    ticketDTO.price,
                    ticket.type,
                    ticket.name,
                    ticketDTO.minAge,
                    ticketDTO.maxAge,
                    ticketDTO.start_period,
                    ticketDTO.end_period,
                    ticketDTO.duration
                )
            } else { //ticketDTO.type == "seasonal"
                // update (add/remove active columns: min_age, max_age, start_period, end_period)
                // or modify some values (price, name, min_age, max_age, start_period, end_period, duration)
                Ticket(
                    ticket.ticketId,
                    ticketDTO.price,
                    ticket.type,
                    ticketDTO.name,
                    ticketDTO.minAge,
                    ticketDTO.maxAge,
                    ticketDTO.start_period,
                    ticketDTO.end_period,
                    ticketDTO.duration
                )
            }
        ticketRepository.save(ticketEntity) //'save' performs an update if the row exists

        /* Alternative rough method */
        /*
        //It does not update, but delete the row and recreate a new one with a new ticket id
        ticketRepository.deleteById(ticket?.ticketId!!)
        ticketRepository.save(ticketEntity)
         */
    }


    //






    /*private fun contactPaymentService(request: PaymentInfoDTO) {
        try {
            log.info("Receiving product request")
            log.info("Sending message to Kafka {}", request)
            val message: Message<PaymentInfoDTO> = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, topicPayment)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
            //ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }*/

    private fun contactTravelerService(request: UsernameDTO) {
        try {
            log.info("Receiving product request")
            log.info("Sending message to Kafka {}", request)
            val message: Message<UsernameDTO> = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, topicTraveler)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplateTraveler.send(message)
            log.info("Message sent with success")
            //ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }


    private fun checkValidityOfValidFrom(type: String, name: String, validFrom: String, ticket: Ticket) {

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

        } else {
//            println(validFrom)
//            println(ticket.start_period!!.toLocalDate().toString().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            if (validFrom<ticket.start_period!!.toLocalDate().toString().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) || validFrom>ticket.end_period!!.toLocalDate().toString().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                throw BadRequestException("NotBefore must be in the validity period of seasonal types")

        }
    }


}

