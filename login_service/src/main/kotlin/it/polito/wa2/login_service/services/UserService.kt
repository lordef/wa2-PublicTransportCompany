package it.polito.wa2.lab3group04.services

import it.polito.wa2.lab3group04.dtos.ActivationDTO
import it.polito.wa2.lab3group04.dtos.RegistrationRequestDTO
import it.polito.wa2.lab3group04.dtos.UserDTO

interface UserService{
    fun createUser(userDTO: RegistrationRequestDTO) : ActivationDTO
    fun validateUser(activation: ActivationDTO): UserDTO
}