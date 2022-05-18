package it.polito.wa2.traveler_service.services.impl

import it.polito.wa2.traveler_service.dtos.*
import it.polito.wa2.traveler_service.entities.UserDetails
import it.polito.wa2.traveler_service.repositories.UserDetailsRepository
import it.polito.wa2.traveler_service.services.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.polito.wa2.traveler_service.exceptions.NotFoundException
import java.util.*
import it.polito.wa2.traveler_service.entities.TicketPurchased
import it.polito.wa2.traveler_service.exceptions.BadRequestException
import it.polito.wa2.traveler_service.repositories.TicketPurchasedRepository
import it.polito.wa2.traveler_service.security.JwtUtils
import org.springframework.beans.factory.annotation.Value
import java.text.SimpleDateFormat


@Service
@Transactional
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
            /*var date : Date?
            var formatter: SimpleDateFormat
            if(userDetailsDTO.date_of_birth==null)
                date= null
            else {*/
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                val date = formatter.parse(userDetailsDTO.date_of_birth)
            //}



            val userDetailsEntity = UserDetails(
                    userDetailsDTO.username,
                    userDetailsDTO.name,
                    userDetailsDTO.address,
                    date,
                    userDetailsDTO.telephone_number
            )

            return userDetailsRepository.save(userDetailsEntity).toDTO()
        }catch(ex: Exception){
            throw BadRequestException("Problems during insertion of User Details")
        }

    }

    override fun getUserTickets(username: String): List<TicketPurchasedDTO> {
        val userDetails = userDetailsRepository.findByUsername(username)

        if(userDetails==null)
            throw NotFoundException("Username not found")

        return ticketPurchasedRepository.findAllByUserDetailsUsername(username).map{it->it.toDTO(jwtUtils)}

    }

    override fun postUserTickets(username: String, purchasedTicketDTO: PurchaseTicketDTO): List<TicketPurchasedDTO> {
        var numberOfTickets = purchasedTicketDTO.quantity
        val ticketsList = mutableListOf<TicketPurchasedDTO>()
        val userDetails = userDetailsRepository.findByUsername(username)

        if(userDetails==null)
            throw NotFoundException("Username not found")


        if(purchasedTicketDTO.quantity<1)
            throw BadRequestException("Cannot request a not positive number of tickets")

        //ticket creation
        do {
            val ticket = TicketPurchased(
                Date(),
                Date(Date().time + ticketExpirationMs),
                purchasedTicketDTO.zone,
                    "",//TODO merging with master
                userDetails
            )
            ticketsList.add(ticketPurchasedRepository.save(ticket).toDTO(jwtUtils))

            numberOfTickets--
        } while (numberOfTickets > 0)

        return ticketsList
    }

    override fun getTravelers() {
    }

}