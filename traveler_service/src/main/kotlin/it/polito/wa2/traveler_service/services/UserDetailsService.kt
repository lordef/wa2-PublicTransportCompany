package it.polito.wa2.traveler_service.services

import it.polito.wa2.traveler_service.dtos.TicketPurchasedDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.dtos.PurchaseTicketDTO
import it.polito.wa2.traveler_service.entities.TicketPurchased

interface UserDetailsService {
    fun getUserProfile(username : String) : UserDetailsDTO
    fun putUserProfile(userDetailsDTO: UserDetailsDTO) : UserDetailsDTO
    fun getUserTickets(username: String) : List<TicketPurchasedDTO>
    fun postUserTickets(username: String, purchaseTicketDTO: PurchaseTicketDTO) : List<TicketPurchasedDTO>

    //admin services
    fun getTravelers()
    fun getTravelerProfile()
    fun getTravelerTickets()
}