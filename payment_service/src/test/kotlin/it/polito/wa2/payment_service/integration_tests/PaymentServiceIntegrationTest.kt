package it.polito.wa2.payment_service.integration_tests


import it.polito.wa2.payment_service.services.impl.PaymentCheckListener
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.hamcrest.CoreMatchers
import org.junit.ClassRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.TimeUnit


/**
 * This test class uses Testcontainers to instantiate and manage an external Apache
 * Kafka broker hosted inside a Docker container.
 *
 * Therefore, one of the prerequisites for using Testcontainers is that Docker is installed on the host running this test
 *
 */
@RunWith(SpringRunner::class)
@Import(it.polito.wa2.payment_service.integration_tests.KafkaTestContainersLiveTest::class)
@SpringBootTest(classes = [PaymentCheckListener::class])
@DirtiesContext
class KafkaTestContainersLiveTest {
    @Autowired
    var template: KafkaTemplate<String, String>? = null

    @Autowired
    private val consumer: KafkaConsumer<*, *>? = null

    @Autowired
    private val producer: KafkaProducer<*, *>? = null

    @Value("\${test.topic}")
    private val topic: String? = null
    @Test
    fun givenKafkaDockerContainer_whenSendingtoDefaultTemplate_thenMessageReceived() {
        //TODO try implement this function
        template!!.send(topic!!, "Sending with default template")
        consumer.getLatch().await(10000, TimeUnit.MILLISECONDS)
        assertThat(consumer.getLatch().getCount(), CoreMatchers.equalTo(0L))
        assertThat(consumer.getPayload(), CoreMatchers.containsString("embedded-test-topic"))
    }


    @TestConfiguration
    internal class KafkaTestContainersConfiguration {
        @Bean
        fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
            val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
            factory.consumerFactory = consumerFactory()
            return factory
        }

        @Bean
        fun consumerFactory(): ConsumerFactory<in String, in Any> {
            return DefaultKafkaConsumerFactory(consumerConfigs())
        }

        @Bean
        fun consumerConfigs(): Map<String, Any> {
            val props: MutableMap<String, Any> = HashMap()
            props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] =
                kafka.bootstrapServers
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
            props[ConsumerConfig.GROUP_ID_CONFIG] = "pbc"
            props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            return props
        }

        @Bean
        fun producerFactory(): ProducerFactory<String, String> {
            val configProps: MutableMap<String, Any> = HashMap()
            configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] =
                kafka.bootstrapServers
            configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            return DefaultKafkaProducerFactory(configProps)
        }

        @Bean
        fun kafkaTemplate(): KafkaTemplate<String, String> {
            return KafkaTemplate(producerFactory())
        }
    }

    companion object {
        @ClassRule
        var kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3")) //TODO try with :latest
        //TODO try to create zookeper
    }
}

