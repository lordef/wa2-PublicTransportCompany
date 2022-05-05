package it.polito.wa2.lab3group04.dtos

import it.polito.wa2.lab3group04.annotations.ValidPassword
import it.polito.wa2.lab3group04.entities.Role
import it.polito.wa2.lab3group04.entities.User
import org.springframework.security.core.userdetails.UserDetails

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class UserDTO(
        val userId : Long?,
        var nickname : String,
        private val password : String?,
        var email : String,
        private val roles: Set<Role>, //TODO deve essere per forza un set?
        val active: Boolean
): UserDetails {
    override fun getAuthorities(): MutableSet<Role> {
        return roles.toMutableSet()
    }

    override fun getPassword(): String? {
        return password?:""
    }

    override fun getUsername(): String {
        return nickname
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
        return active
    }
}

fun User.toDTO() : UserDTO {
    return UserDTO(getId(), nickname, password, email,this.getRoles(),active)
}



