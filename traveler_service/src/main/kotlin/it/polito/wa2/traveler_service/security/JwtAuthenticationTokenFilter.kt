package it.polito.wa2.traveler_service.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationTokenFilter : OncePerRequestFilter() {

    @Value( "\${application.jwt.jwtHeaderStart}" )
    lateinit var prefix: String

    @Value( "\${application.jwt.jwtHeader}" )
    lateinit var header: String

    @Autowired lateinit var jwtUtils : JwtUtils


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authorizationHeader = request.getHeader(header)
            
            if (authorizationHeader == null || !authorizationHeader.startsWith(prefix)) {
                filterChain.doFilter(request, response)
                return
            }

            val jwtToken = authorizationHeader.replace(prefix, "").trim()

            if (!jwtUtils.validateJwt(jwtToken)) {
                filterChain.doFilter(request, response)
                return
            }

            val userDetails = jwtUtils.getDetailsJwt(jwtToken)

            val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
            )
            authentication.details = WebAuthenticationDetailsSource()
                    .buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication

            println("jwt valido")
        }catch (e: Exception){
            println(e)
        }
        filterChain.doFilter(request, response)

    }

}