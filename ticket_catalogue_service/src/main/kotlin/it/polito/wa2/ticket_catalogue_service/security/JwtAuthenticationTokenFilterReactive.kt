package it.polito.wa2.ticket_catalogue_service.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
    }
}


@Component
class JwtServerAuthenticationConverter(private val jwtUtils: JwtUtils) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> {



        val result = exchange?.request?.headers?.get("Authorization")
            ?.firstOrNull {
                it.startsWith("Bearer")
            }
            ?.let {
                val jwt = jwtUtils.getJwtTokenFromHeader(it)
                if (jwtUtils.validateJwt(jwt))
                    jwtUtils.getDetailsJwt(jwt)
                else
                    null
            }
        return Mono.justOrEmpty(result)
            .map { UsernamePasswordAuthenticationToken(it.username, null, it.roles) }
    }
}

