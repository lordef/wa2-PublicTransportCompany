package it.polito.wa2.ticket_catalogue_service.controllers

import it.polito.wa2.ticket_catalogue_service.dtos.OrderDTO
import it.polito.wa2.ticket_catalogue_service.dtos.PurchaseTicketsRequestDTO
import it.polito.wa2.ticket_catalogue_service.dtos.TicketDTO
import it.polito.wa2.ticket_catalogue_service.exceptions.BadRequestException
import it.polito.wa2.ticket_catalogue_service.services.impl.TicketCatalogueServiceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid



@RestController
class TicketCatalogueController {

    @Autowired
    lateinit var catalogueService: TicketCatalogueServiceImpl

    @GetMapping("/tickets", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getTickets() : Flow<TicketDTO> {
        return catalogueService.getAllTickets()
    }

    @PostMapping("/shop")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).CUSTOMER)")
    suspend fun purchaseTickets(
        principal: Principal,
        @RequestBody @Valid purchaseRequestDTO: PurchaseTicketsRequestDTO
    ): Long {

        /*if (!validDate(purchaseRequestDTO.expirationDate) || !notFutureDate(purchaseRequestDTO.expirationDate))
            throw BadRequestException("Wrong json date field")

        if (!validDate(purchaseRequestDTO.notBefore) || !notFutureDate(purchaseRequestDTO.notBefore))
            throw BadRequestException("Wrong json date field")*/

        println(purchaseRequestDTO)

        val res = catalogueService.purchaseTickets(principal.name, purchaseRequestDTO).awaitSingle()
        return res

    }

    // GET /orders →Get list of all user orders
    @GetMapping("/orders", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).CUSTOMER)")
    fun getOrders(principal: Principal): Flow<OrderDTO> {
        return catalogueService.getOrdersByUserId(principal.name)
    }

    // GET /orders/{order-id} →Get a specific order
    @GetMapping("/orders/{orderId}", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).CUSTOMER)")
    suspend fun getOrder(principal: Principal, @PathVariable("orderId") orderId: Long): OrderDTO {
        val res = catalogueService.getOrderByOrderIdAndUserId(principal.name, orderId).awaitSingleOrNull()
        if(res==null)
            throw BadRequestException("User and/or Order Not Present")
        else return res
    }

    @PostMapping("/admin/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).ADMIN)")
    suspend fun addTicket(
        @RequestBody @Valid ticketDTO: TicketDTO
    ) {
        /*if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")*/

        catalogueService.addTicket(ticketDTO)
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).ADMIN)")
    fun getOrdersByAllUsers(): Flow<OrderDTO> {
        return catalogueService.getAllOrdersByAllUsers()
    }

    @GetMapping("/admin/orders/{userId}")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.ticket_catalogue_service.dtos.Role).ADMIN)")
    fun getOrderBySpecificUser(@PathVariable("userId") userId: String): Flow<OrderDTO> {
        return catalogueService.getOrdersByUserId(userId)
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