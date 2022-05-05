package it.polito.wa2.lab3group04.controllers

import it.polito.wa2.lab3group04.dtos.ActivationDTO
import it.polito.wa2.lab3group04.dtos.LoginRequestDTO
import it.polito.wa2.lab3group04.dtos.RegistrationRequestDTO
import it.polito.wa2.lab3group04.exceptions.BadRequestException
import it.polito.wa2.lab3group04.security.Jwt
import it.polito.wa2.lab3group04.services.impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid





@RestController
class RegistrationController() {

    @Value( "\${application.jwt.jwtHeaderStart}" )
    lateinit var prefix: String

    @Autowired
    lateinit var jwt: Jwt

    @Autowired
    lateinit var userService: UserServiceImpl

    @Autowired
    lateinit var authenticationManager: AuthenticationManager



    @PostMapping("/user/register")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun userRegistration(
            @RequestBody @Valid userDTO: RegistrationRequestDTO,
            bindingResult: BindingResult
    ): UserRegistrationResponseBody {

        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        //creating user
        val activationDTO = userService.createUser(userDTO)

        return UserRegistrationResponseBody(activationDTO.provisional_id,userDTO.email)

    }

    @PostMapping("/user/validate")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    fun userValidation(@RequestBody @Valid activationDTO: ActivationDTO,
                       bindingResult: BindingResult) : UserValidationResponseBody {

        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        val user = userService.validateUser(activationDTO)

        return UserValidationResponseBody(user.userId, user.nickname, user.email)
    }




    @PostMapping("/user/login")
    fun login(
            @RequestBody @Valid loginRequest: LoginRequestDTO, bindingResult: BindingResult,
            response: HttpServletResponse
    )/*: UserDetails*/ {
        if (bindingResult.hasErrors())
            throw BadRequestException(bindingResult.fieldErrors.joinToString())


        val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.nickname, loginRequest.password)
        )

        response.setHeader("Authorization", "$prefix ${jwt.generateJwt(authentication)}")

        //return (authentication.principal as UserDetailsDTO).copy(password = null)*/
    }

}

data class UserRegistrationResponseBody(val provisional_id : UUID?, val email : String )
data class UserValidationResponseBody(val userId: Long?, val nickname: String, val email: String)