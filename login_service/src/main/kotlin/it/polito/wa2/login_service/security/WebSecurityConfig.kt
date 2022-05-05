package it.polito.wa2.lab3group04.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var userDetailsService: UserDetailsService

    override fun configure (auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
    }

    override fun configure(http: HttpSecurity) {

        //without this, always return 403 Forbidden for any request
        http
                .csrf().disable() // disable csrf

        http
                .authorizeRequests()
                .antMatchers("/user/**")
                .permitAll()


    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }



}