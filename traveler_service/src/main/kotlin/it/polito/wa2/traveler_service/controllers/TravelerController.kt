package it.polito.wa2.traveler_service.controllers


import it.polito.wa2.traveler_service.dtos.PurchaseTicketDTO
import it.polito.wa2.traveler_service.dtos.TicketAcquiredDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.exceptions.BadRequestException
import it.polito.wa2.traveler_service.services.impl.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
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
    fun getMyProfile(): GetMyProfileResponseBody {

        val userName = SecurityContextHolder.getContext().authentication.name
        val userDetailsDTO = userDetailsService.getUserProfile(userName)

        return GetMyProfileResponseBody(
            userDetailsDTO.name,
            userDetailsDTO.address,
            userDetailsDTO.telephone_number,
            userDetailsDTO.date_of_birth
        )
    }

    @PutMapping("/my/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    fun putMyProfile(
        @RequestBody @Valid userDetailsDTO: UserDetailsDTO,
        bindingResult: BindingResult
    ) {
        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        //if date not null and not empty , the only valid format is dd-MM-yyyy
        if ((userDetailsDTO.date_of_birth != null && userDetailsDTO.date_of_birth != "") && !validDate(userDetailsDTO.date_of_birth as String))
            throw BadRequestException("Wrong json date field")


        //adding username to the DTO
        userDetailsDTO.username = SecurityContextHolder.getContext().authentication.name
        //putting user info in the db
        userDetailsService.putUserProfile(userDetailsDTO)

    }

    @GetMapping("/my/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    fun getMyTickets(): List<TicketAcquiredDTO> {
        return userDetailsService.getUserTickets(SecurityContextHolder.getContext().authentication.name)
    }

    @PostMapping("/my/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).SERVICE)")
    @ResponseBody
    fun postMyTickets(
        @RequestBody @Valid purchaseTicketDTO: PurchaseTicketDTO,
        bindingResult: BindingResult
    )/*: List<TicketAcquiredDTO> */{
        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        println(purchaseTicketDTO)

        //posting tickets in the db
        userDetailsService.postUserTickets(
            SecurityContextHolder.getContext().authentication.name,
            purchaseTicketDTO
        )
    }


    @GetMapping("/admin/travelers")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getAdminTravelers(): List<String> {
        return userDetailsService.getTravelers()
    }

    @GetMapping("/admin/traveler/{userID}/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getAdminProfile(@PathVariable("userID") userID: String): UserDetailsDTO {
        return userDetailsService.getUserProfile(userID)
    }

    @GetMapping("/admin/traveler/{userID}/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getAdminTickets(@PathVariable("userID") userID: String): List<TicketAcquiredDTO> {
        return userDetailsService.getUserTickets(userID)
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

data class GetMyProfileResponseBody(
    val name: String?,
    val address: String?,
    val telephone_number: String?,
    val date_of_birth: String?
) {}
