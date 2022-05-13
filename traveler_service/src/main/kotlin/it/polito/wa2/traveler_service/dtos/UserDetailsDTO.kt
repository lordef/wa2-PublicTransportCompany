package it.polito.wa2.traveler_service.dtos


import it.polito.wa2.traveler_service.entities.UserDetails
import org.springframework.format.annotation.DateTimeFormat
import java.text.SimpleDateFormat
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
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
    @field:NotEmpty(message = "date of birth must not be empty")
    @field:NotNull
    @field:Pattern(regexp = "([0-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-[0-9]{4}")
    /*@field:Pattern(regexp = """
        ((
        ((^(([0][1-9])|([1-2][0-9])|([3][01]))-(([0][13578])|([1][02])))|
        (([0][1-9]|[1-2][0-9]|[3][0])-([0][469]|[1][1]))
        )-([0-9]{4}))|
        (^([0][1-9]|[1-2][0-9])-([0][2])-([0-9]{2}(([02468][048])|([13579][26]))))|
        (^([0][1-9]|[1][0-9]|2[0-8])-([0][2])-([0-9][0-9](([0-9][13579])|([13579][048])|([02468][26])))))
    """)*/
    var date_of_birth: String? = null,
) {}

fun UserDetails.toDTO(): UserDetailsDTO {
    val formatter = SimpleDateFormat("dd-MM-yyyy")
    val date = formatter.format(date_of_birth)
    return UserDetailsDTO(username, name, address, telephone_number, date)
}