package it.polito.wa2.ticket_catalogue_service.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${kafka.bootstrapAddress}")
    private val servers: String,
    @Value("\${kafka.topics.bank_check}")
    private val topic: String,
    @Value("\${kafka.topics.customer_check}")
    private val topicTraveler: String
) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }

    @Bean
    fun newtopic(): NewTopic {
        return NewTopic(topic, 1, 1.toShort())
    }

    @Bean
    fun newtopicTraveler(): NewTopic {
        return NewTopic(topicTraveler, 1, 1.toShort())
    }
}