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

        //TODO va bene tornare come sub lo username? Uso quello (lo username) come chiava primaria del relativo record nel db del traveler?
        // perchè se serve necessariamente l'id della tabella user come chiave primaria dell'altra tabella nel
        // traveler, allora va ritornato anche l'id (sebbene il testo dica che le uniche info devono essere sub e roles, però malnati
        // aveva detto su slack che la coeerenza è garantito da quella.. tuttavia è pur vero che lo username è univoco e può essere
        // usato come chiave primaria dell'altra
        return Jwts.builder()
                .setSubject(userPrincipal.username)
                .setIssuedAt(Date())
                .setExpiration(Date(Date().time + jwtExpirationMs))
                .claim("roles", userPrincipal.authorities.map { it.toString() })
                .signWith(key)
                .compact()
    }

}