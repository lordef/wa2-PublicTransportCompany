package it.polito.wa2.traveler_service.integration_tests

import it.polito.wa2.traveler_service.dtos.PurchaseTicketDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.entities.TicketAcquired
import it.polito.wa2.traveler_service.entities.UserDetails
import it.polito.wa2.traveler_service.repositories.TicketPurchasedRepository
import it.polito.wa2.traveler_service.repositories.UserDetailsRepository
import it.polito.wa2.traveler_service.security.JwtUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

//TODO risistemare i test secondo modifiche apportate
/*
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerMyTicketsIntegrationTests {

    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:latest").apply {
            withDatabaseName("db2")
            withUsername("postgres")
            withPassword("postgres")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 8080

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository



    /** 3) GET /my/tickets/ **/

    @Test
    fun validGetMyTicketTest() {
        val userDetailsDTO = UserDetailsDTO("customer1", "name", "address")
        val userDetails = UserDetails(userDetailsDTO.username, userDetailsDTO.name, userDetailsDTO.address)
        userDetailsRepository.save(userDetails)

        val ticketWithoutJws = TicketAcquired(
            Date(),
            Date(Date().time + 3600000),
            "ABC",
            "",
            userDetails
        )

        ticketPurchasedRepository.save(ticketWithoutJws)

        ticketWithoutJws.jws = jwtUtils.generateJwt(
            ticketWithoutJws.getId() as Long,
            ticketWithoutJws.issuedAt,
            ticketWithoutJws.expiry,
            ticketWithoutJws.zoneId
        )


        val baseUrl = "http://localhost:$port/my/tickets"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )

        val entity = HttpEntity("", headers)
        val response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

    }

    @Test
    fun noUserDetailsGetMyTicketTest() {
        val baseUrl = "http://localhost:$port/my/tickets"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjIiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm9sZXMiO" +
                    "lsiQ1VTVE9NRVIiXX0.oBF7ct8jLBfpMfe6oev6utrcborJL8RCwxJLt_GbpwMyvad_o-qdy_3UuilCWqyTU_gO-HKyzL9DstpU0eT3Fg"
        )

        val entity = HttpEntity("", headers)
        val response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)

    }

    /**    4) POST /my/tickets **/
    @Test
    fun validPutMyTicketTest() {
        val userDetailsDTO = UserDetailsDTO("customer1", "name", "address")
        val userDetails = UserDetails(userDetailsDTO.username, userDetailsDTO.name, userDetailsDTO.address)
        userDetailsRepository.save(userDetails)

        val purchaseTicketDTO = PurchaseTicketDTO("buy_tickets", 3, "ABC")

        val baseUrl = "http://localhost:$port/my/tickets"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )
        val entity = HttpEntity(purchaseTicketDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

    }

    @Test
    fun invalidCmdInjsonPutMyTicketTest() {
        val userDetailsDTO = UserDetailsDTO("customer1", "name", "address")
        val userDetails = UserDetails(userDetailsDTO.username, userDetailsDTO.name, userDetailsDTO.address)
        userDetailsRepository.save(userDetails)

        val purchaseTicketDTO = PurchaseTicketDTO("buy tickets", 3, "ABC")

        val baseUrl = "http://localhost:$port/my/tickets"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )
        val entity = HttpEntity(purchaseTicketDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

    }

    @Test
    fun noUserDetailsPutMyTicketTest() {
        val purchaseTicketDTO = PurchaseTicketDTO("buy tickets", 3, "ABC")

        val baseUrl = "http://localhost:$port/my/tickets"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjIiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm9sZXMiO" +
                    "lsiQ1VTVE9NRVIiXX0.oBF7ct8jLBfpMfe6oev6utrcborJL8RCwxJLt_GbpwMyvad_o-qdy_3UuilCWqyTU_gO-HKyzL9DstpU0eT3Fg"
        )
        val entity = HttpEntity(purchaseTicketDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)

    }

    @Test
    fun invalidQuantityPutMyTicketTest() {
        val userDetailsDTO = UserDetailsDTO("customer1", "name", "address")
        val userDetails = UserDetails(userDetailsDTO.username, userDetailsDTO.name, userDetailsDTO.address)
        userDetailsRepository.save(userDetails)


        val purchaseTicketDTO = PurchaseTicketDTO("buy tickets", -2, "ABC")

        val baseUrl = "http://localhost:$port/my/tickets"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )
        val entity = HttpEntity(purchaseTicketDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

    }


}*/