package it.polito.wa2.ticket_catalogue_service.services.impl

import it.polito.wa2.ticket_catalogue_service.dtos.PaymentInfoDTO
import it.polito.wa2.ticket_catalogue_service.dtos.UserDetailsDTO
import it.polito.wa2.ticket_catalogue_service.entities.Order
import it.polito.wa2.ticket_catalogue_service.entities.PaymentInfo
import it.polito.wa2.ticket_catalogue_service.entities.Status
import it.polito.wa2.ticket_catalogue_service.entities.Ticket
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.repositories.OrderRepository
import it.polito.wa2.ticket_catalogue_service.repositories.TemporaryPaymentRepository
import it.polito.wa2.ticket_catalogue_service.repositories.TicketRepository

import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.messaging.Message
import java.time.LocalDate
import java.time.Period

@Component
class TravelerAnswerListener(
    @Value("\${kafka.topics.customer_check}")
    val topicTraveler: String,


    @Autowired
    private val kafkaTemplatePayment: KafkaTemplate<String, Any>
) {


    val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var paymentRepository: TemporaryPaymentRepository

    private val logger = LoggerFactory.getLogger(javaClass)



    @KafkaListener(topics = ["\${kafka.topics.customer_check_answer}"], groupId = "tca")
    fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received {}", consumerRecord)
        ack.acknowledge()

        val userInfo = consumerRecord.value() as UserDetailsDTO

        val orderEntity: Order?

        val ticket: Ticket?

        val paymentInfo: PaymentInfo?

        runBlocking {
            orderEntity = orderRepository.findById(userInfo.orderId)
        }

        if (orderEntity == null)
            throw BadRequestException("Not Existing Order")

        orderEntity.status = Status.USER_CHECKED

        runBlocking {
            orderRepository.save(orderEntity)
        }

        runBlocking {
            ticket = ticketRepository.findById(orderEntity.ticketType)
        }


        //se è necessario controllare l'età...
        if (ticket!!.maxAge != null || ticket!!.minAge != null) {
            //throw an exception if age constraints are not satisfied
            checkAgeConstraints(userInfo, ticket)
        }

        runBlocking {
            paymentInfo = paymentRepository.findByOrderId(userInfo.orderId)
        }



            //Contacting Payment Service
            val request = PaymentInfoDTO(
                orderEntity.totalPrice,
                paymentInfo!!.creditCardNumber,
                paymentInfo!!.expirationDate,
                paymentInfo!!.cvv,
                paymentInfo!!.cardHolder,
                orderEntity.userId,
                orderEntity.orderId as Long
            )

            contactPaymentService(request)

            runBlocking {
                paymentRepository.deleteByOrderId(userInfo.orderId)
            }

        }





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
                .setHeader(KafkaHeaders.TOPIC, topicTraveler)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplatePayment.send(message)
            log.info("Message sent with success")
            //ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }
}