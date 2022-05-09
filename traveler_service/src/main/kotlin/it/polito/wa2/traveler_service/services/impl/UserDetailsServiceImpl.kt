package it.polito.wa2.traveler_service.services.impl

import it.polito.wa2.traveler_service.dtos.TicketPurchasedDTO
import it.polito.wa2.traveler_service.dtos.UserDetailsDTO
import it.polito.wa2.traveler_service.dtos.toDTO
import it.polito.wa2.traveler_service.entities.UserDetails
import it.polito.wa2.traveler_service.repositories.UserDetailsRepository
import it.polito.wa2.traveler_service.services.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.polito.wa2.traveler_service.exceptions.NotFoundException

@Service
@Transactional
@Configuration
@EnableScheduling
@EnableAsync
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    override fun getUserProfile (username: String) : UserDetailsDTO {
      val userDetailsDTO = userDetailsRepository.findByUsername(username)?.toDTO()

      if(userDetailsDTO==null) {
          throw NotFoundException("Username not found")
      }

      return userDetailsDTO
    }

    override fun putUserProfile (userDetailsDTO: UserDetailsDTO) : UserDetailsDTO {
        val userDetailsEntity = UserDetails(userDetailsDTO.username, userDetailsDTO.name, userDetailsDTO.address, userDetailsDTO.date_of_birth, userDetailsDTO.telephone_number)
        return userDetailsRepository.save(userDetailsEntity).toDTO()
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