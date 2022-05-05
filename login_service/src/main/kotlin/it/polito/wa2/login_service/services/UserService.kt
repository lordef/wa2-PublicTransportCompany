package it.polito.wa2.login_service.services

import it.polito.wa2.login_service.dtos.ActivationDTO
import it.polito.wa2.login_service.dtos.RegistrationRequestDTO
import it.polito.wa2.login_service.dtos.UserDTO

interface UserService{
    fun createUser(userDTO: RegistrationRequestDTO) : ActivationDTO
    fun validateUser(activation: ActivationDTO): UserDTO
}