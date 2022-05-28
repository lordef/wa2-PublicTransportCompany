package it.polito.wa2.ticket_catalogue_service.controllers

import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.services.impl.TicketCatalogueServiceImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Function
import javax.validation.Valid


@RestController
class TicketCatalogueController {

    @Autowired
    lateinit var catalogueService: TicketCatalogueServiceImpl

    @GetMapping("/tickets", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getTickets() : Flow<TicketDTO> {
        return catalogueService.getAllTickets().onEach { delay(5000) }
    }

    @PostMapping("/shop/{ticketId}")
    suspend fun purchaseTickets(@AuthenticationPrincipal  principal : Mono<UserDetails>,  @RequestBody @Valid purchaseRequestDTO: PurchaseTicketsRequestDTO )/*: ProductDTO*/{

        val user = principal.map{ it.getUsername()}.block()

        if(!validDate(purchaseRequestDTO.expirationDate))
            throw BadRequestException("Wrong json date field")

        println(purchaseRequestDTO)

        catalogueService.purchaseTickets(user as String, purchaseRequestDTO)
        //return
    }



    /***** TODO: consuming mono *****+*/
    @GetMapping("/todo")
    suspend fun current(@AuthenticationPrincipal principal: Mono<UserDetails>): Mono<Map<String, Any>> {
        return principal.map { user ->
            java.util.Map.of(
                "name", user.username,
                "roles", AuthorityUtils.authorityListToSet(user.authorities)
            )

        }
    }
    /***** end - TODO: consuming mono *****+*/


    @PostMapping("/admin/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).ADMIN)")
    suspend fun addTicket(){
        println("aggiunto!!")
    }



    //the only valid format is dd-MM-yyyy
    private fun validDate(date: String): Boolean {
        val stringParsed = date.split("-")
        val day = stringParsed.get(0).toInt()
        val month = stringParsed.get(1).toInt()
        val year = stringParsed.get(2).substring(2, 4).toInt()
        val firstCheck = when (month) {
            //if February
            2 -> {
                if (year % 4 == 0) {//se bisestile (if leap)
                    if (day > 29)
                        false
                    else true
                } else {
                    if (day > 28)
                        false
                    else true
                }

            }
            //if April
            4 -> {
                if (day > 30)
                    false
                else true
            }
            //if June
            6 -> {
                if (day > 30)
                    false
                else true
            }
            //if September
            9 -> {
                if (day > 30)
                    false
                else true
            }
            //if November
            11 -> {
                if (day > 30)
                    false
                else true
            }
            else -> true
        }
        if (firstCheck && notFutureDate(date))
            return true
        else return false
    }

    private fun notFutureDate(date: String): Boolean {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(date)

        if (date.compareTo(formatter.parse(formatter.format(Date()))) <= 0)
            return true
        else return false
    }
}