package it.polito.wa2.traveler_service.services.impl

import it.polito.wa2.traveler_service.dtos.*
import it.polito.wa2.traveler_service.repositories.TicketPurchasedRepository
import it.polito.wa2.traveler_service.repositories.TransitRepository
import it.polito.wa2.traveler_service.services.AdminReportsService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminReportsServiceImpl : AdminReportsService {

    @Autowired
    lateinit var transitRepository: TransitRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    override fun getTicketsAcquiredByUser(userID : String, dateRangeDTO: DateRangeDTO) : Flow<TicketAcquiredDTO> {
        return ticketPurchasedRepository.getTicketsAcquiredByUser(userID, dateRangeDTO).map { it.toDTO() }
    }

    override fun getTicketsAcquired(dateRangeDTO: DateRangeDTO) :  Flow<TicketAcquiredDTO> {
        return ticketPurchasedRepository.getTicketsAcquired(dateRangeDTO).map { it.toDTO()  }
    }

    override fun getTransitsByUser(userID : String, dateTimeRangeDTO: DateTimeRangeDTO) : Flow<TransitDTO> {
        return transitRepository.getTransitsByUser(userID, dateTimeRangeDTO).map { it.toDTO() }
    }

    override fun getTransits(dateTimeRangeDTO: DateTimeRangeDTO) : Flow<TransitDTO> {
        return transitRepository.getTransits(dateTimeRangeDTO).map { it.toDTO()  }
    }
}