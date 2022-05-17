package it.polito.wa2.login_service.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import it.polito.wa2.login_service.dtos.UserDTO

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class Jwt {

    @Value("\${application.jwt.jwtSecret}")
    private lateinit var jwtSecret: String

    @Value("\${application.jwt.jwtExpirationMs}")
    var jwtExpirationMs: Long = -1

    private val key: Key by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun generateJwt(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDTO

        return Jwts.builder()
                .setSubject(userPrincipal.username)
                .setIssuedAt(Date())
                .setExpiration(Date(Date().time + jwtExpirationMs))
                .claim("roles", userPrincipal.authorities.map { it.toString() })
                .signWith(key)
                .compact()
    }

}