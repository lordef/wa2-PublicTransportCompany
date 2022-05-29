package it.polito.wa2.payment_service.dtos

import org.springframework.security.core.GrantedAuthority

enum class Role: GrantedAuthority {
    CUSTOMER,
    ADMIN,
    SERVICE;

    override fun getAuthority(): String {
        return this.name
    }
}