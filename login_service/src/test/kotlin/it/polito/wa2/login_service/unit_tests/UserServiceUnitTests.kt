package it.polito.wa2.lab3group04.unit_tests

import it.polito.wa2.lab3group04.dtos.ActivationDTO
import it.polito.wa2.lab3group04.dtos.RegistrationRequestDTO
import it.polito.wa2.lab3group04.exceptions.*
import it.polito.wa2.lab3group04.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class UserServiceUnitTests : SpringTestBase() {

    @Autowired
    lateinit var userService: UserService

    @Test
    fun successfulCreateUser() {
        val user = RegistrationRequestDTO(1, "jhk", "rqwhjfhajsjk", "prova123@gmail.com")

        Assertions.assertInstanceOf(ActivationDTO::class.java, userService.createUser(user))
    }

    @Test
    fun failedSameNicknameCreateUser() {
        val user = RegistrationRequestDTO(123, "prova1", "password", "polito123@gmail.com")

        Assertions.assertThrows(BadRequestException::class.java) {
            userService.createUser(user)
        }
    }

    @Test
    fun failedSameEmailCreateUser() {
        val user = RegistrationRequestDTO(123, "prova123", "password", "polito@gmail.com")

        Assertions.assertThrows(BadRequestException::class.java) {
            userService.createUser(user)
        }
    }

    @Test
    fun successfulValidateUser() {
        val activation = ActivationDTO(activation1.provisionalUserId, activation1.activationCode)

        Assertions.assertInstanceOf(RegistrationRequestDTO::class.java, userService.validateUser(activation))
    }

    @Test
    fun invalidActivationCodeValidateUser() {
        val activation = ActivationDTO(activation2.provisionalUserId, -1)

        Assertions.assertThrows(NotFoundException::class.java) {
            userService.validateUser(activation)
        }

    }

    @Test
    fun invalidActivationCodeLastTimeValidateUser() {
        val activation = ActivationDTO(activation4.provisionalUserId, -1)

        for (i in 1..4) {
            try {
                userService.validateUser(activation)
            } catch (Ex: Exception) { continue }
        }

        Assertions.assertThrows(NotFoundException::class.java) {
            userService.validateUser(activation)
        }
    }

    @Test
    fun activationHasExpiredValidateUser() {
        //Setting expirationDate in fase di creazione di activation3 in SpringTestBase
        val activation = ActivationDTO(activation3.provisionalUserId, activation3.activationCode)

        Assertions.assertThrows(NotFoundException::class.java) {
            userService.validateUser(activation)
        }
    }


    @Test
    fun failedNotFoundProvisionalIDValidateUser() {
        val activation = ActivationDTO(UUID.randomUUID(), 2197897987)

        Assertions.assertThrows(NotFoundException::class.java) {
            userService.validateUser(activation)
        }

    }
}