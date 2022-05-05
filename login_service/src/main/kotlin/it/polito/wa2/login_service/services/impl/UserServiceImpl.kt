package it.polito.wa2.lab3group04.services.impl

import it.polito.wa2.lab3group04.dtos.ActivationDTO
import it.polito.wa2.lab3group04.dtos.RegistrationRequestDTO
import it.polito.wa2.lab3group04.dtos.UserDTO
import it.polito.wa2.lab3group04.dtos.toDTO
import it.polito.wa2.lab3group04.entities.Activation
import it.polito.wa2.lab3group04.entities.User
import it.polito.wa2.lab3group04.exceptions.*
import it.polito.wa2.lab3group04.repositories.ActivationRepository
import it.polito.wa2.lab3group04.repositories.UserRepository
import it.polito.wa2.lab3group04.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import java.util.*


@Service
@Transactional
@Configuration
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(name=["scheduler.enabled"], matchIfMissing = true)
class UserServiceImpl: UserDetailsService,UserService {

    @Autowired lateinit var mailService : EmailServiceImpl
    @Autowired lateinit var activationRepository: ActivationRepository
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var passwordEncoder: PasswordEncoder


    override fun loadUserByUsername(username: String): UserDTO {
        val user = userRepository.findByNickname(username) ?: throw UnauthorizedException("Username not found")
        if(!user.active) throw UnauthorizedException("User not active")
        return user.toDTO()
    }


    override fun createUser(userDTO: RegistrationRequestDTO): ActivationDTO{

        try {
            if (userRepository.findByNickname(userDTO.nickname) != null)
                throw BadRequestException("Username already in use")
            if (userRepository.findByEmail(userDTO.email) != null)
                throw BadRequestException("Email already in use")

            //encode the password before to store it
            val user = User(userDTO.nickname, passwordEncoder.encode(userDTO.password as String), userDTO.email)

            val savedUser = userRepository.save(user)

            val act = Activation(user)

            activationRepository.save(act) //memorizzo l'activation nel DB

            val ret = mailService.sendEmail(savedUser.email, "Email verification",
                    "Hi ${savedUser.nickname},\n" +
                            "This is your Activation Code : \n" +
                            "${act.activationCode} \n" +
                            "Use it to activate your account.\n" +
                            "Pay attention, this activation code will remain active up to 24 hours")

            if (ret == false)
                throw BadRequestException("Problem Occurs during mail sending")

            return act.toDTO()
        }catch(ex: Exception){
            throw BadRequestException(ex.message.toString())
        }
    }


    override fun validateUser(activation: ActivationDTO): UserDTO {
        try {
            /*
             * retrieve from repository the Activation starting from the provisionalId String.
             * Verify if it is not expired and enable the corresponding nickname.
             * If the activation record is not found or it is expired, throw an exception.
             */
            val act = activationRepository.findByProvisionalUserId(activation.provisional_id as UUID)
            if(act==null)
                throw NotFoundException("Provisional Id Not Found")
            if(act.isExpired()) {
                userRepository.delete(act.user)
                activationRepository.delete(act)
                throw NotFoundException("Activation has expired")
            }


            //In case of existing a not expired activation, but mismatched act. code
            if (act.activationCode != activation.activation_code) {
                if (act.attemptCounter == 1) {
                    userRepository.delete(act.user)
                    activationRepository.delete(act)
                } else {
                    act.attemptCounter -= 1
                    activationRepository.save(act)
                }
                throw NotFoundException("Mismatching activation code")
            }

            activationRepository.delete(act)
            act.user.active = true
            userRepository.save(act.user)

            return act.user.toDTO()
        }catch(ex: Exception) {
            throw NotFoundException(ex.message.toString())
        }
    }

    @Scheduled(fixedRate = 86400000)// 24hours expressed in milliseconds
    @Async
    @Transactional
    fun pruningExpiredRegistrationData(){
        val expiredActivations = activationRepository.
                                  findActivationsByExpirationDateIsBefore( Date(System.currentTimeMillis()))

        if(expiredActivations.toList().isEmpty())
            return

        for(act in expiredActivations){
            userRepository.delete(act.user)
            activationRepository.delete(act)
        }
    }

}
