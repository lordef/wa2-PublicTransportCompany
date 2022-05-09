package it.polito.wa2.traveler_service.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.traveler_service.dtos.Role
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtils {

    @Value("\${application.jwt.jwtSecret}")
    private lateinit var jwtSecret: String

    @Autowired lateinit var  userDetailsRepository : UserDetailsRepository

    private val key: Key by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun validateJwt(authToken: String): Boolean {
        try {
            Jwts.parserBuilder()
                    .setAllowedClockSkewSeconds(30) // Allows a skew of 30 seconds between the timestamp inside the JWT
                    //and the current one on the local machine
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken)

            //Now we can safely trust the JWT (its integrity and authenticity)
            return true
        } catch (ex: Exception) {
            // we *cannot* use the JWT as authentication
            println(ex.message) //TODO: retrieve from lab03
            return false
        }
    }


    fun getDetailsJwt(authToken: String): UserDetailsJwt {

        val jwtBody = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken).body

        val bodySub = jwtBody.subject
        val bodyRoles = jwtBody["roles"]

        if(bodySub == null || bodyRoles== null)
            throw Exception() //TODO creare una exception specifica magari

        val roles = ( bodyRoles as List<String>).map { Role.valueOf(it) }.toSet()

        return UserDetailsJwt(bodySub, roles = roles)

    }

}

data class UserDetailsJwt( val subject : String, val roles : Set<Role> )