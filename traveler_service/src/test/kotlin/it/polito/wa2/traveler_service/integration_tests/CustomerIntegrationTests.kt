package it.polito.wa2.traveler_service.integration_tests

import it.polito.wa2.traveler_service.dtos.PurchaseTicketDTO
import it.polito.wa2.traveler_service.dtos.TicketPurchasedDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.entities.TicketPurchased
import it.polito.wa2.traveler_service.entities.UserDetails
import it.polito.wa2.traveler_service.repositories.TicketPurchasedRepository
import it.polito.wa2.traveler_service.repositories.UserDetailsRepository
import it.polito.wa2.traveler_service.security.JwtUtils
import org.hibernate.annotations.common.util.impl.Log
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


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerIntegrationTests {

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


    /** GET /my/profile  **/

    @Test
    fun validGetMyProfileTest() {
        val userDetailsDTO = UserDetailsDTO("customer1", "name", "address")
        val userDetails = UserDetails(userDetailsDTO.username, userDetailsDTO.name, userDetailsDTO.address)
        userDetailsRepository.save(userDetails)

        val baseUrl = "http://localhost:$port/my/profile"

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
    fun noDataForUserdetailsGetMyProfileTest() {
        userDetailsRepository.deleteAll()

        val baseUrl = "http://localhost:$port/my/profile"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )

        val entity = HttpEntity("", headers)
        val response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)

    }


    /** 2) PUT /my/profile **/

    @Test
    fun validPutMyProfileTest() {

        val userDetailsDTO = UserDetailsDTO(null, "name", "address", "3774632969", "29-02-2020")

        val baseUrl = "http://localhost:$port/my/profile"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )
        val entity = HttpEntity(userDetailsDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.PUT, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

    }


    @Test
    fun invalidLeapYearPutMyProfileTest() {

        val userDetailsDTO = UserDetailsDTO(null, "name", "address", "3774632969", "29-02-1998")

        val baseUrl = "http://localhost:$port/my/profile"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )
        val entity = HttpEntity(userDetailsDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.PUT, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

    }

    @Test
    fun validEmptyDtoPutMyProfileTest() {

        val userDetailsDTO = UserDetailsDTO(null, null, null, null, null)

        val baseUrl = "http://localhost:$port/my/profile"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )
        val entity = HttpEntity(userDetailsDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.PUT, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

    }

    @Test
    fun invalidDataFormatPutMyProfileTest() {

        val userDetailsDTO = UserDetailsDTO(null, "name", "address", "3774632969", "04/04/1998")

        val baseUrl = "http://localhost:$port/my/profile"

        val headers = HttpHeaders()

        headers.setBearerAuth(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )
        val entity = HttpEntity(userDetailsDTO, headers)

        val response = restTemplate.exchange(baseUrl, HttpMethod.PUT, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

    }

//    3) GET /my/tickets/
//    3.1) Autorizzazione fallita nel caso di : jwt errato, jwt scaduto e ruolo customer non presente : se fallisce torna 401 (forse 403 se il ruolo è assente)
//    3.2) caso in cui l'utente che ha fatto la GET non è ancora nel db userDetails (non ha ancora fatto una PUT per inserire nel db il suo username (basta sia presente il record con il suo username)) : torna 404
//    3.3) caso in cui va tutto liscio : torna 200 OK e la lista di tickets comprati da quello user

    @Test
    fun validGetMyTicketTest() {
        val userDetailsDTO = UserDetailsDTO("customer1", "name", "address")
        val userDetails = UserDetails(userDetailsDTO.username, userDetailsDTO.name, userDetailsDTO.address)
        userDetailsRepository.save(userDetails)

        val ticketWithoutJws = TicketPurchased(
            Date(),
            Date(Date().time + 3600000),
            "ABC",
            "",
            userDetails
        )

        ticketPurchasedRepository.save(ticketWithoutJws)

        ticketWithoutJws.jws = jwtUtils.generateJwt(ticketWithoutJws.getId() as Long, ticketWithoutJws.issuedAt, ticketWithoutJws.expiry, ticketWithoutJws.zoneId)



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
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lcjEiLCJpYXQiOjE2NTI4OTE4NzksImV4cCI6MTcxNjA1MTI2Miwicm" +
                    "9sZXMiOlsiQ1VTVE9NRVIiXX0.W71JOUP-TSK_j__yDz3XlWJbtO7UD3_5ZVs7BVQXg2EqKwHeW9J7d9NHpVAOVDpHtTyuuJWoBmA26jQ9wyP78g"
        )

        val entity = HttpEntity("", headers)
        val response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)

    }

//    4) POST /my/tickets
//
//    4.1) Autorizzazione fallita nel caso di : jwt errato, jwt scaduto e ruolo customer non presente : se fallisce torna 401 (forse 403 se il ruolo è assente)
//    4.2) caso in cui l'utente che ha fatto la POST non è ancora nel db userDetails (non ha ancora fatto una PUT per inserire nel db il suo username (basta sia presente il record con il suo username)) : torna 404
//    4.3) caso in cui la quantity nel body è <1 (torna 400)
//    4.4) caso in cui cmd è diverso da "buy_tickets" (torna 400)
//    4.5) caso in cui va tutto bene : ritorna una lista di tot biglietti come body, e 200 OK




}