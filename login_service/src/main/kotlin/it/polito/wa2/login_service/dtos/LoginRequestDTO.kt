package it.polito.wa2.lab3group04.dtos

import it.polito.wa2.lab3group04.annotations.ValidPassword
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class LoginRequestDTO (
    @field:Size(max = 30, message = "username is too long")
    @field:NotEmpty(message = "nickname must not be empty")
    @field:NotNull
    var nickname : String,

    @field:NotEmpty(message = "password must not be empty")
    @field:NotNull
    @ValidPassword
    var password : String,
){}