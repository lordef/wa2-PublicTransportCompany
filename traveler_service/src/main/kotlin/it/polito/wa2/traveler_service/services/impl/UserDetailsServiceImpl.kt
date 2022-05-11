package it.polito.wa2.traveler_service.services.impl

import it.polito.wa2.traveler_service.dtos.*
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
import java.util.*
import it.polito.wa2.traveler_service.entities.TicketPurchased
import it.polito.wa2.traveler_service.repositories.TicketPurchasedRepository
import it.polito.wa2.traveler_service.security.JwtUtils
import org.springframework.beans.factory.annotation.Value
import java.text.SimpleDateFormat


@Service
@Transactional
@Configuration
@EnableScheduling
@EnableAsync
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Value("\${application.jwt.jwtExpirationMs}")
    val ticketExpirationMs: Long = -1

    override fun getUserProfile(username: String): UserDetailsDTO {
        val userDetailsDTO = userDetailsRepository.findByUsername(username)?.toDTO()

        if (userDetailsDTO == null) {
            throw NotFoundException("Username not found")
        }

        return userDetailsDTO
    }

    override fun putUserProfile(userDetailsDTO: UserDetailsDTO): UserDetailsDTO {
        try {
            val formatter = SimpleDateFormat("dd-MM-yyyy")
            val date = formatter.parse(userDetailsDTO.date_of_birth)

            val userDetailsEntity = UserDetails(
                    userDetailsDTO.username,
                    userDetailsDTO.name,
                    userDetailsDTO.address,
                    date,
                    userDetailsDTO.telephone_number
            )

            return userDetailsRepository.save(userDetailsEntity).toDTO()
        }catch(ex: Exception){
            //TODO mettere un eccezione specifica
            throw Exception()
        }

    }

    override fun getUserTickets(username: String): List<TicketPurchasedDTO> {
        return mutableListOf()
    }

    override fun postUserTickets(username: String, purchasedDTO: PurchaseTicketDTO): List<TicketPurchasedDTO> {
        var numberOfTickets = purchasedDTO.quantity
        val ticketsList = mutableListOf<TicketPurchasedDTO>()
        val userDetails = userDetailsRepository.findByUsername(username)

        if(userDetails==null){
            throw NotFoundException("Username not found")
        }

        //ticket creation
        do {
            val ticket = TicketPurchased(
                Date(),
                Date(Date().time + ticketExpirationMs),
                purchasedDTO.zone,
                userDetails
            )
            ticketsList.add(ticketPurchasedRepository.save(ticket).toDTO(jwtUtils))

            numberOfTickets--
        } while (numberOfTickets > 0)

        return ticketsList
    }

    override fun getTravelers() {
    }

    override fun getTravelerProfile() {
    }

    override fun getTravelerTickets() {
    }

}