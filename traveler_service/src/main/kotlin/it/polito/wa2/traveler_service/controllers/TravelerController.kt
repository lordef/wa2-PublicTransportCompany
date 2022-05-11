package it.polito.wa2.traveler_service.controllers


import it.polito.wa2.traveler_service.dtos.PurchaseTicketDTO
import it.polito.wa2.traveler_service.dtos.TicketPurchasedDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.exceptions.BadRequestException
import it.polito.wa2.traveler_service.services.impl.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid


@RestController
class TravelerController {

    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    @GetMapping("/my/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getMyProfile() : GetMyProfileResponseBody {

            val userName = SecurityContextHolder.getContext().authentication.name
            val userDetailsDTO = userDetailsService.getUserProfile(userName)

            return GetMyProfileResponseBody(userDetailsDTO.name, userDetailsDTO.address, userDetailsDTO.telephone_number, userDetailsDTO.date_of_birth)
    }

    @PutMapping("/my/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    //TODO controllare la validazione sul formato data ricevuta
    fun putMyProfile(
        @RequestBody @Valid userDetailsDTO : UserDetailsDTO,
        bindingResult: BindingResult
    ){
        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        if(/*userDetailsDTO.date_of_birth!=null &&*/ !validDate(userDetailsDTO.date_of_birth as String))
            throw BadRequestException("Wrong json date field")


        //adding username to the DTO
        userDetailsDTO.username = SecurityContextHolder.getContext().authentication.name
        //putting user info in the db
        userDetailsService.putUserProfile(userDetailsDTO)

    }

    @GetMapping("/my/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getMyTickets(): List<TicketPurchasedDTO>{
        return userDetailsService.getUserTickets(SecurityContextHolder.getContext().authentication.name)
    }

    @PostMapping("/my/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun postMyTickets(
        @RequestBody @Valid purchaseTicketDTO: PurchaseTicketDTO,
        bindingResult: BindingResult
    ) : List<TicketPurchasedDTO>{
        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        //posting tickets in the db
        return userDetailsService.postUserTickets(SecurityContextHolder.getContext().authentication.name, purchaseTicketDTO)
    }

    @GetMapping("/admin/travelers")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getAdminTravelers() {

    }

    @GetMapping("/admin/traveler/{userID}/profile")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getAdminProfile(@PathVariable userID: String) {

    }

    @GetMapping("/admin/traveler/{userID}/tickets")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getAdminTickets(@PathVariable userID: String) {

    }

    private fun validDate(date: String): Boolean{
        val stringParsed = date.split("-")
        val day = stringParsed.get(0).toInt()
        val month = stringParsed.get(1).toInt()
        val year = stringParsed.get(2).substring(2,4).toInt()
        println(day)
        println(month)
        println(year)
        val firstCheck = when(month){
            //if February
            2 ->{
                if(year%4==0){//se bisestile (if leap)
                    if(day>29)
                        false
                    else true
                }else{
                    if(day>28)
                        false
                    else true
                }

            }
            //if April
            4 -> {
                if(day>30)
                    false
                else true
            }
            //if June
            6 -> {
                if(day>30)
                    false
                else true
            }
            //if September
            9 -> {
                if(day>30)
                    false
                else true
            }
            //if November
            11 -> {
                if(day>30)
                    false
                else true
            }
            else -> true
        }
        if(firstCheck && notFutureDate(date))
            return true
        else return false
    }

    private fun notFutureDate(date: String):Boolean{
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(date)

        if(date.compareTo(formatter.parse(formatter.format(Date()))) <=0)
            return true
        else return false
    }

}

data class GetMyProfileResponseBody(val name: String?, val address : String?, val telephone_number : String?, val date_of_birth : String?) {}
