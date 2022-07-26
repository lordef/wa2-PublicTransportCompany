package it.polito.wa2.traveler_service.services

import it.polito.wa2.traveler_service.dtos.TicketAcquiredDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.dtos.PurchaseTicketDTO
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono


interface UserDetailsService {
    suspend fun getUserProfile(username : String) : Mono<UserDetailsDTO>
    suspend fun putUserProfile(userDetailsDTO: UserDetailsDTO) : Mono<UserDetailsDTO>
    fun getUserTickets(username: String) : Flow<TicketAcquiredDTO>
    suspend fun getTicketById(ticketId : Long, username: String) : Mono<TicketAcquiredDTO>
    suspend fun postUserTickets(username: String, purchaseTicketDTO: PurchaseTicketDTO)

    //admin services
    fun getTravelers(): Flow<String>

}