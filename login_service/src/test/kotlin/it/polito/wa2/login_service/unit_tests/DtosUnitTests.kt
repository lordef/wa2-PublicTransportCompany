package it.polito.wa2.lab3group04.unit_tests

import it.polito.wa2.lab3group04.dtos.ActivationDTO
import it.polito.wa2.lab3group04.dtos.RegistrationRequestDTO
import it.polito.wa2.lab3group04.dtos.toDTO
import it.polito.wa2.lab3group04.entities.Activation
import it.polito.wa2.lab3group04.entities.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class DtosUnitTests {

    @Test
    fun matchingUserToDto() {
        val userEntity = User("jack", "passW0rD", "email@gmail.com")

        val expectedUserDto = RegistrationRequestDTO(null, "jack", null, "email@gmail.com")

        Assertions.assertEquals(expectedUserDto, userEntity.toDTO())
    }

    @Test
    fun matchingActivationToDto() {
        val userEntity = User("jack", "passW0rD", "email@gmail.com")
        val activationEntity = Activation(userEntity)
        activationEntity.provisionalUserId = UUID.randomUUID()

        val expectedActivationDto = ActivationDTO(activationEntity.provisionalUserId, activationEntity.activationCode)

        Assertions.assertEquals(expectedActivationDto, activationEntity.toDTO())
    }


    @Test
    fun matchingActivationWithNullProvisionalUserIdToDto() {
        val userEntity = User()
        val activationEntity = Activation(userEntity)

        val expectedActivationDto = ActivationDTO(null, activationEntity.activationCode)

        Assertions.assertEquals(expectedActivationDto, activationEntity.toDTO())
    }


    @Test
    fun notMatchingActivationToDto() {
        val userEntity = User()
        val activationEntity = Activation(userEntity)

        val expectedActivationDto = ActivationDTO(null, null)

        Assertions.assertNotEquals(expectedActivationDto, activationEntity.toDTO())

    }

}