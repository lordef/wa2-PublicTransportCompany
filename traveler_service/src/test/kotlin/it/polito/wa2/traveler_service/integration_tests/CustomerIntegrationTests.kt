package it.polito.wa2.traveler_service.integration_tests

import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.entities.UserDetails
import it.polito.wa2.traveler_service.repositories.UserDetailsRepository
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
    lateinit var userDetailsRepository: UserDetailsRepository


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


    /*
    2) PUT /my/profile (il body può anche essere o completamente vuoto, o solo con alcuni campi : tanto lo username lo recupera dal jwt, e non dal body,
    mentre tutti i campi non specificati vengono messi a null nel db)

    2.1) Autorizzazione fallita nel caso di : jwt errato, jwt scaduto e ruolo customer non presente : se fallisce torna 401 (forse 403 se il ruolo è assente)
    2.2) caso in cui, se presente la data nel body, il formato data non è valido (formato ammesso dd-MM-yyyy e basta) (il campo date_of_birth assente, o presente ma messo a null o a "" nel body, è valido : viene messo a null in automatico nel db) : se fallisce torna 400
    2.3) caso in cui, se presente la data nel body e non sia null o "", la data sia corretta (es 31-04 o 29-02 in anni non bisestili): se fallisce torna 400
    2.4) validazione campi UserDetailsDTO ricevuto (i campi non possono essere NotNull o NotEmpty : possono anche essere assenti e vengono messi a null nel db (controlalre questo con un test)): se fallisce torna 400
    2.5) caso in cui è tutto ok (ritorna solo 200 OK)

     */

    @Test
    fun codeNotMatchValReqTest() {
        //creating user to validate his account
        val userDTO = UserDTO( null, "username_ValTest2", "Username@2021!", "email_ValTest2@gmail.com")
        val user = User(userDTO.nickname, userDTO.password as String, userDTO.email, null, false)
        userRepository.save(user)
        //creating provisionalID and activation code
        val activation = Activation(user)
        val activationSaved = activationRepository.save(activation)

        //Creating client validation request
        val activationRequest = ActivationDTO( activationSaved.provisionalUserId,123456)

        val baseUrl = "http://localhost:$port/user"
        val request = HttpEntity(activationRequest)
        val response = restTemplate.postForEntity<Unit>("$baseUrl/validate", request)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

}