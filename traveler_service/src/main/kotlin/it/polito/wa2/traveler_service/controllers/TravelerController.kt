package it.polito.wa2.traveler_service.controllers


import org.springframework.web.bind.annotation.*

@RestController
class TravelerController {

    @GetMapping("/my/profile")
    fun getMyProfile() {
        try {
            println("riuscito")
            /*if (jwt == null) {
                return ResponseEntity.status(401).body("unauthenticated")
            }

            val body = Jwts.parserBuilder().setSigningKey("secretKey").build().parseClaimsJws(jwt)

            return ResponseEntity.ok(body)*/
        }catch (e: Exception){
            //return ResponseEntity.status(401).body("unauthenticated")
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