package it.polito.wa2.ticket_catalogue_service.services.impl

import it.polito.wa2.ticket_catalogue_service.dtos.PaymentInfoAnswerDTO
import it.polito.wa2.ticket_catalogue_service.entities.Order
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.repositories.OrderRepository
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class PaymentAnswerListener {

    @Autowired
    private lateinit var orderRepository: OrderRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["\${kafka.topics.bank_check_answer}"], groupId = "pbca")
    fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received {}", consumerRecord)
        ack.acknowledge()

        val order = consumerRecord.value() as PaymentInfoAnswerDTO

        val orderEntity: Order?

        runBlocking {
            orderEntity = orderRepository.findById(order.orderId)
        }

        if (orderEntity == null)
            throw BadRequestException("Not Existing Order")

        orderEntity.status = order.status

        runBlocking {
            orderRepository.save(orderEntity)
        }
    }

}
