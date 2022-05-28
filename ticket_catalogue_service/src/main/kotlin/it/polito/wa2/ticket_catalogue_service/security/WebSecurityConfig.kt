package it.polito.wa2.ticket_catalogue_service.security

import org.springframework.context.annotation.Bean
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class WebSecurityConfig {

    fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/tickets", "/todo") //TODO: delete TODO
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity,
                                  jwtAuthenticationManager: ReactiveAuthenticationManager,
                                  jwtAuthenticationConverter: ServerAuthenticationConverter
    ): SecurityWebFilterChain {

        val authenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter)


        http
            .authorizeExchange()
            .pathMatchers("/admin/")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers("/orders/")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers("/shop/**")
            .authenticated()
            .and()
            .authorizeExchange()
            .anyExchange()
            .permitAll()




        http
            .cors().disable()
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .exceptionHandling()
            .authenticationEntryPoint(handler)
            .accessDeniedHandler(handler)

        http
            .addFilterAt(
                authenticationWebFilter,
                SecurityWebFiltersOrder.AUTHENTICATION)

        return http.build()
    }

}



private val handler = {
        swe: ServerWebExchange, e : Exception ->
    println(e)
    println(e.message)
    swe.response.statusCode = HttpStatus.UNAUTHORIZED
    swe.response.headers.contentType = MediaType.APPLICATION_JSON
    swe.response.writeWith(
        Jackson2JsonEncoder().encode(
            Mono.just("Not Authorized User"),
            swe.response.bufferFactory(),
            ResolvableType.forInstance("Not Authorized User"),
            MediaType.APPLICATION_JSON,
            Hints.from(Hints.LOG_PREFIX_HINT, swe.logPrefix)
        )
    )
}