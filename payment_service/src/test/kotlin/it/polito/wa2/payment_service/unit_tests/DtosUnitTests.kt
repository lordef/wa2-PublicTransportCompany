package it.polito.wa2.payment_service.unit_tests

import it.polito.wa2.payment_service.dtos.TransactionDTO
import it.polito.wa2.payment_service.dtos.toDTO
import it.polito.wa2.payment_service.entities.Status
import it.polito.wa2.payment_service.entities.Transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SpringBootTest
class DtosUnitTests {

    @Test
    fun matchingTransactionToDto() {
        val transactionEntity = Transaction(1, 2f, "testCustomer", 1, LocalDateTime.now(), Status.PENDING, "asbcdefghil", "04-04-2024", "789", "Test Customer" )

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        val date = LocalDateTime.now().format(formatter)

        val expectedTransactionDto = TransactionDTO(1, 2f, "testCustomer", 1, date, Status.PENDING, "asbcdefghil", "04-04-2024", "789", "Test Customer")

        Assertions.assertEquals(expectedTransactionDto, transactionEntity.toDTO())
    }

// TODO : Test deserializer for PaymentInfoDto https://github.com/magnuspedro/KafkaConsumer/blob/main/src/test/kotlin/com/magnuspedro/kafka/consumer/KafkaConsumer/entities/deserializer/ProductDeserializerTest.kt

//    @Test
//    fun matchingUserToDto() {
//        val userEntity = User("jack", "passW0rD", "email@gmail.com")
//
//        val expectedUserDto = UserDTO(null, "jack", "passW0rD", "email@gmail.com",userEntity.roles,false)
//
//        Assertions.assertEquals(expectedUserDto, userEntity.toDTO())
//    }
//
//    @Test
//    fun matchingActivationToDto() {
//        val userEntity = User("jack", "passW0rD", "email@gmail.com")
//        val activationEntity = Activation(userEntity)
//        activationEntity.provisionalUserId = UUID.randomUUID()
//
//        val expectedActivationDto = ActivationDTO(activationEntity.provisionalUserId, activationEntity.activationCode)
//
//        Assertions.assertEquals(expectedActivationDto, activationEntity.toDTO())
//    }
//
//
//    @Test
//    fun matchingActivationWithNullProvisionalUserIdToDto() {
//        val userEntity = User()
//        val activationEntity = Activation(userEntity)
//
//        val expectedActivationDto = ActivationDTO(null, activationEntity.activationCode)
//
//        Assertions.assertEquals(expectedActivationDto, activationEntity.toDTO())
//    }
//
//
//    @Test
//    fun notMatchingActivationToDto() {
//        val userEntity = User()
//        val activationEntity = Activation(userEntity)
//
//        val expectedActivationDto = ActivationDTO(null, null)
//
//        Assertions.assertNotEquals(expectedActivationDto, activationEntity.toDTO())
//
//    }

}