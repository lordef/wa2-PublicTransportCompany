package it.polito.wa2.traveler_service.controllers


import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.exceptions.BadRequestException
import it.polito.wa2.traveler_service.services.impl.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
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

        //adding username to the DTO
        userDetailsDTO.username = SecurityContextHolder.getContext().authentication.name
        //putting user info in the db
        userDetailsService.putUserProfile(userDetailsDTO)

    }

    @GetMapping("/my/tickets")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getMyTickets() {

    }

    @PostMapping("/my/tickets")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun postMyTickets() {

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


}

data class GetMyProfileResponseBody(val name: String?, val address : String?, val telephone_number : String?, val date_of_birth : String?) {}
