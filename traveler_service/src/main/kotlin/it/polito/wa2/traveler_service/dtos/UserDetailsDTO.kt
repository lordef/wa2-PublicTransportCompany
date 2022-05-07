package it.polito.wa2.traveler_service.dtos


import it.polito.wa2.traveler_service.entities.UserDetails
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class UserDetailsDTO(
    val userDetailsId: String,

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
    var telephon_number: String? = null,

    //contraints for date
    @field:NotEmpty(message = "telephon_number must not be empty")
    @field:NotNull
    var date_of_birth: String? = null,

    //TODO vedere eventuali constraint
    val roles: Set<Role>?=null
): org.springframework.security.core.userdetails.UserDetails {
    override fun getAuthorities(): MutableSet<Role> {
        return roles!!.toMutableSet()
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return userDetailsId
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}

fun UserDetails.toDTO(): UserDetailsDTO {
    return UserDetailsDTO(username, name, address, telephon_number, date_of_birth,null)
}