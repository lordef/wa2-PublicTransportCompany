package it.polito.wa2.traveler_service.controllers


import it.polito.wa2.traveler_service.dtos.Role
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class TravelerController {

    @GetMapping("/my/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.entities.Role).ADMIN)")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getMyProfile() {
        try {
            /*if(SecurityContextHolder.getContext().authentication.authorities.contains(Role.CUSTOMER)) {
                println("riuscito")
            } else {
                println("non autorizzato")
            }*/

            println("riuscito")
            println(SecurityContextHolder.getContext().authentication.authorities)
        }catch (e: Exception){

        }

        return
    }

    @PutMapping("/my/profile")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun putMyProfile() {

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