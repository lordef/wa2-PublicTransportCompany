package it.polito.wa2.traveler_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
class TravelerServiceApplication

fun main(args: Array<String>) {
    runApplication<TravelerServiceApplication>(*args)
}
