package it.polito.wa2.ticket_catalogue_service.dtos

import org.springframework.security.core.GrantedAuthority

enum class Role: GrantedAuthority {
    CUSTOMER,
    ADMIN;

    override fun getAuthority(): String {
        return this.name
    }
}