package it.polito.wa2.login_service.dtos

import it.polito.wa2.login_service.entities.Role
import it.polito.wa2.login_service.entities.User
import org.springframework.security.core.userdetails.UserDetails

data class UserDTO(
        val userId : Long?,
        var nickname : String,
        private val password : String?,
        var email : String,
        private val roles: Set<Role>,
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



