package it.polito.wa2.payment_service.services.impl

import it.polito.wa2.payment_service.dtos.PaymentInfoAnswerDTO
import it.polito.wa2.payment_service.dtos.PaymentInfoDTO
import it.polito.wa2.payment_service.entities.Status
import it.polito.wa2.payment_service.entities.Transaction
import it.polito.wa2.payment_service.repositories.TransactionRepository
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
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.validation.Valid


@Component
class PaymentCheckListener(
    @Value("\${kafka.topics.bank_check_answer}")
    val answerTopic: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    @Autowired
    private lateinit var transactionRepository: TransactionRepository


    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["\${kafka.topics.bank_check}"], groupId = "pbc")
    fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received {}", consumerRecord)
        ack.acknowledge()

        val message = consumerRecord.value() as PaymentInfoDTO
        println(message)

        val bodyResult = manageBankTransaction(message)

        contactCatalogueService(bodyResult)
    }



private fun contactCatalogueService(answer: PaymentInfoAnswerDTO) {
    try {
        logger.info("Receiving product request")
        logger.info("Sending message to Kafka {}", answer)
        val message: Message<PaymentInfoAnswerDTO> = MessageBuilder
            .withPayload(answer)
            .setHeader(KafkaHeaders.TOPIC, answerTopic)
            .setHeader("X-Custom-Header", "Custom header here")
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")
        //ResponseEntity.ok().build()
    } catch (e: Exception) {
        logger.error("Exception: {}", e)
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
    }
}

    private fun manageBankTransaction(@Valid message: PaymentInfoDTO) : PaymentInfoAnswerDTO{

        val bodyResult: PaymentInfoAnswerDTO

        var transaction = Transaction(
            null,
            message.totalAmount,
            message.username,
            message.orderId,
            LocalDateTime.now(),
            Status.PENDING,
            message.creditCardNumber,
            message.expirationDate,
            message.cvv,
            message.cardHolder
        )


        runBlocking {
            transaction=transactionRepository.save(transaction)
        }

        //simulating interaction with the Bank Service, providing a random result (approved or denied)
        val rnds = (0..100).random()
        if (rnds % 2 == 0) {
            bodyResult = PaymentInfoAnswerDTO(Status.DENIED, message.orderId)
            transaction.status=Status.DENIED
        } else{
            bodyResult = PaymentInfoAnswerDTO(Status.ACCEPTED, message.orderId)
            transaction.status=Status.ACCEPTED
        }



        runBlocking {
            transactionRepository.save(transaction)
        }

        return bodyResult
    }
}