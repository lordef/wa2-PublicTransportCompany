package it.polito.wa2.traveler_service.configKafka

import it.polito.wa2.traveler_service.dtos.Serializer.CatalogueSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfig(
    @Value("\${kafka.bootstrapAddress}")
    private val servers: String
) {



    /** ---------------- Traveler Kafka Producer ------------------- **/
    @Bean
    fun producerFactoryTraveler(): ProducerFactory<String, Any> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = CatalogueSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplateTraveler(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactoryTraveler())
    }
}

