package it.polito.wa2.traveler_service.entities

import org.springframework.security.core.GrantedAuthority

enum class Role: GrantedAuthority {
    CUSTOMER,
    ADMIN;

    override fun getAuthority(): String {
        return this.name
    }
}