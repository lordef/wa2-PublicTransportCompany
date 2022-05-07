package it.polito.wa2.traveler_service.controllers


import it.polito.wa2.traveler_service.dtos.Role
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class TravelerController {

    @GetMapping("/my/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    fun getMyProfile() {
        try {

                println("riuscito")

        }catch (e: Exception){
        }

    }

    @PutMapping("/my/profile")
    fun putMyProfile() {

    }

    @GetMapping("/my/tickets")
    fun getMyTickets() {

    }

    @PostMapping("/my/tickets")
    fun postMyTickets() {

    }

    @GetMapping("/admin/travelers")
    fun getAdminTravelers() {

    }

    @GetMapping("/admin/traveler/{userID}/profile")
    fun getAdminProfile(@PathVariable userID: String) {

    }

    @GetMapping("/admin/traveler/{userID}/tickets")
    fun getAdminTickets(@PathVariable userID: String) {

    }


}