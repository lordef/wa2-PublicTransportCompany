package it.polito.wa2.traveler_service.dtos


import it.polito.wa2.traveler_service.entities.UserDetails
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class UserDetailsDTO(
    @field:Size(max = 30, message = "name is too long")
    var username: String? = null,

    //TODO check contraints

    @field:Size(max = 30, message = "name is too long")
    @field:NotEmpty(message = "name must not be empty")
    @field:NotNull
    var name: String? = null,

    @field:Size(max = 30, message = "address is too long")
    @field:NotEmpty(message = "address must not be empty")
    @field:NotNull
    var address: String? = null,

    @field:Size(max = 10, message = "telephon_number is too long")
    @field:NotEmpty(message = "telephon_number must not be empty")
    @field:NotNull
    var telephone_number: String? = null,

    //contraints for date
    @field:NotEmpty(message = "telephon_number must not be empty")
    @field:NotNull
    var date_of_birth: String? = null,
) {}

fun UserDetails.toDTO(): UserDetailsDTO {
    return UserDetailsDTO(username, name, address, telephone_number, date_of_birth)
}