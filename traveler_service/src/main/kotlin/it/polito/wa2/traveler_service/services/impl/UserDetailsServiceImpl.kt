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
import it.polito.wa2.traveler_service.entities.TicketAcquired
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

            val formatter = SimpleDateFormat("dd-MM-yyyy")
            var date : Date? = null

            if(userDetailsDTO.date_of_birth!=null && userDetailsDTO.date_of_birth!="")
                date = formatter.parse(userDetailsDTO.date_of_birth)

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

    override fun getUserTickets(username: String): List<TicketAcquiredDTO> {
        val userDetails = userDetailsRepository.findByUsername(username)

        if(userDetails==null)
            throw NotFoundException("Username not found")

        return ticketPurchasedRepository.findAllByUserDetailsUsername(username).map{it->it.toDTO()}

    }

    override fun postUserTickets(username: String, purchasedTicketDTO: PurchaseTicketDTO)/*: List<TicketAcquiredDTO> */{
        var numberOfTickets = purchasedTicketDTO.quantity

        val userDetails = userDetailsRepository.findByUsername(username)

        if(userDetails==null)
            throw NotFoundException("Username not found")


        if(purchasedTicketDTO.cmd!="buy_tickets")
            throw BadRequestException("Invalid Post Command")




        if(purchasedTicketDTO.validFrom==null || purchasedTicketDTO.validFrom=="")
            throw BadRequestException("Invalid NotBefore Date")

        //business logic for NotBefore And Expiry Time
        val jwtTimeInfo = computeNbfAndExp(purchasedTicketDTO)

        //ticket creation
        do {
            val ticketWithoutJws = TicketAcquired(
                Date(jwtTimeInfo.iat),
                Date(jwtTimeInfo.nbf),
                Date(jwtTimeInfo.exp),
                purchasedTicketDTO.zone,
                purchasedTicketDTO.type,
                    "",
                userDetails
            )

            ticketPurchasedRepository.save(ticketWithoutJws)

            ticketWithoutJws.jws = jwtUtils.generateJwt(ticketWithoutJws.getId() as Long, ticketWithoutJws.issuedAt, ticketWithoutJws.validFrom, ticketWithoutJws.expiry, ticketWithoutJws.zoneId, ticketWithoutJws.type)

            ticketPurchasedRepository.save(ticketWithoutJws)

            numberOfTickets--
        } while (numberOfTickets > 0)

    }

    override fun getTravelers(): List<String> {
        return userDetailsRepository.findUsernames()
    }

    private fun computeNbfAndExp(purchasedTicketDTO : PurchaseTicketDTO): NbfExpInfo {

        val type=purchasedTicketDTO.type
        val name=purchasedTicketDTO.name
        val validFrom=purchasedTicketDTO.validFrom

        val formatter = SimpleDateFormat("dd-MM-yyyy")

        val iat: Long
        val exp: Long
        val nbf: Long

        if(type!="ordinal" && type!="seasonal")
            throw BadRequestException("wrong type")

        if (type == "ordinal") {

            val ticketInfo: NbfExpInfo = when (name) {
                "ordinary" -> {
                    iat = Date().time
                    nbf = iat
                    exp = iat + 4200000 //4200 * 1000 = 70 minutes in  milliseconds

                    NbfExpInfo(iat, nbf, exp)
                }
                "daily" -> {
                    val currentDate = Date()
                    iat = currentDate.time

                    val cal = Calendar.getInstance()
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)

                    nbf = cal.time.time

                    //retrieving midnight of the following day
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    NbfExpInfo(iat, nbf, cal.time.time)
                }
                "weekly" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is a Monday
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                        throw BadRequestException("Invalid ValidFrom field")

                    //valid from midnight of the selected Monday
                    nbf = validFromDate.time

                    //retrieving midnight of the next Monday (with respect to the actual Monday selected)
                    cal.add(Calendar.DAY_OF_YEAR, 7)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)

                    iat = Date().time
                    NbfExpInfo(iat, nbf, cal.time.time)
                }
                "monthly" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is the first of Any Month
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    if (cal.get(Calendar.DAY_OF_MONTH) != cal.getActualMinimum(Calendar.DAY_OF_MONTH))
                        throw BadRequestException("Invalid ValidFrom field")

                    //valid from midnight of the selected first day of any month
                    nbf = validFromDate.time

                    //retrieving midnight of the first day of the next month (with respect to the next month selected)
                    cal.add(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)


                    iat = Date().time
                    NbfExpInfo(iat, nbf, cal.time.time)
                }
                "biannually" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is the first of Any Month
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    if (cal.get(Calendar.DAY_OF_MONTH) != cal.getActualMinimum(Calendar.DAY_OF_MONTH))
                        throw BadRequestException("Invalid ValidFrom field")

                    //valid from midnight of the selected first day of any month
                    nbf = validFromDate.time

                    //retrieving midnight of the first day of the next 6th month (with respect to the month selected)
                    cal.add(Calendar.MONTH, 6)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)


                    iat = Date().time
                    NbfExpInfo(iat, nbf, cal.time.time)
                }
                "yearly" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is the first of Any Month
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate);
                    if (cal.get(Calendar.DAY_OF_MONTH) != cal.getActualMinimum(Calendar.DAY_OF_MONTH))
                        throw BadRequestException("Invalid ValidFrom field")

                    //valid from midnight of the selected first day of any month
                    nbf = validFromDate.time

                    //retrieving midnight of the first day of the next 12th month (with respect to the month selected)
                    cal.add(Calendar.MONTH, 12)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)


                    iat = Date().time
                    NbfExpInfo(iat, nbf, cal.time.time)
                }
                "weekend_pass" -> {
                    val cal = Calendar.getInstance()

                    //check if validFrom is a Saturday or a Sunday
                    val validFromDate = formatter.parse(validFrom)
                    cal.setTime(validFromDate)
                    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY)
                        throw BadRequestException("Invalid ValidFrom field")


                    if (dayOfWeek == Calendar.SATURDAY)
                        cal.add(Calendar.DAY_OF_YEAR, 2)
                    else
                        cal.add(Calendar.DAY_OF_YEAR, 1)

                    //valid from Midnight of the selected week-end day
                    nbf = validFromDate.time

                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)

                    iat = Date().time
                    NbfExpInfo(iat, nbf, cal.time.time)
                }
                else -> throw BadRequestException("Invalid Ticket Type")
            }

            return ticketInfo
        }else{
            if(purchasedTicketDTO.duration==null)
                throw BadRequestException("Wrong duration")
            iat = Date().time
            nbf=iat
            val durationInMillis = purchasedTicketDTO.duration*60*1000
            exp=Date().time+durationInMillis
            return NbfExpInfo(iat, nbf,exp)
        }
    }

}

data class NbfExpInfo(
    val iat: Long,
    val nbf : Long,
    val exp: Long
)