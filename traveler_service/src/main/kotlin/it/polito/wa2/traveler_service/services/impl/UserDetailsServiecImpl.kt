package it.polito.wa2.traveler_service.services.impl

import it.polito.wa2.traveler_service.dtos.TicketPurchasedDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.repositories.UserDetailsRepository
import it.polito.wa2.traveler_service.services.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@Configuration
@EnableScheduling
@EnableAsync
class UserDetailsServiecImpl : UserDetailsService {

    override fun getUserProfile (username: String) : UserDetailsDTO {

      return UserDetailsDTO("TODO")
    }

    override fun putUserProfile (userDetailsDTO: UserDetailsDTO) : UserDetailsDTO {
        return UserDetailsDTO("TODO")
    }

    override fun getUserTickets (username: String) : List<TicketPurchasedDTO> {
        return mutableListOf()
    }

    override fun postUserTickets(tickets : List<TicketPurchasedDTO>) : List<TicketPurchasedDTO> {
        return mutableListOf()
    }

    override fun getTravelers(){
    }

    override fun getTravelerProfile(){
    }

    override fun getTravelerTickets() {
    }

}