package it.polito.wa2.ticket_catalogue_service.controllers

import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.security.UserDetailsJwt
import it.polito.wa2.ticket_catalogue_service.services.impl.TicketCatalogueServiceImpl
import it.polito.wa2.ticket_catalogue_service.services.impl.UserDetailsDTO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import java.security.Principal
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid


@RestController
class TicketCatalogueController {

    @Autowired
    lateinit var catalogueService: TicketCatalogueServiceImpl

    @GetMapping("/tickets", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getTickets(): Flow<TicketDTO> {
        return catalogueService.getAllTickets().onEach { delay(5000) }
    }

    @PostMapping("/shop/{ticketId}")
    suspend fun purchaseTickets(
        principal: Principal,
        @RequestBody @Valid purchaseRequestDTO: PurchaseTicketsRequestDTO
    )/*: ProductDTO*/ {

        println(principal.name)

        if (!validDate(purchaseRequestDTO.expirationDate))
            throw BadRequestException("Wrong json date field")

        println(purchaseRequestDTO)

        catalogueService.purchaseTickets(principal.name, purchaseRequestDTO)


    }


    /***** TODO: consuming mono *****+*/
    @GetMapping("/todo")
    suspend fun current(): String {
        val principal = ReactiveSecurityContextHolder.getContext()
            .doOnEach { obj: Signal<SecurityContext> -> println(obj.get()?.authentication?.principal) }
        //.map { obj: SecurityContext -> println(obj.authentication.principal);  obj.authentication.principal as UserDetailsJwt }
        return "ciao"
    }

    /***** end - TODO: consuming mono *****+*/


    @PostMapping("/admin/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).ADMIN)")
    suspend fun addTicket() {
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