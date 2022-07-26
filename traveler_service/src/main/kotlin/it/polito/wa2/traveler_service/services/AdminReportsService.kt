package it.polito.wa2.traveler_service.services

import it.polito.wa2.traveler_service.dtos.DateRangeDTO
import it.polito.wa2.traveler_service.dtos.DateTimeRangeDTO
import it.polito.wa2.traveler_service.dtos.TicketAcquiredDTO
import it.polito.wa2.traveler_service.dtos.TransitDTO
import kotlinx.coroutines.flow.Flow

interface AdminReportsService {

    fun getTicketsAcquiredByUser(userID : String, dateRangeDTO: DateRangeDTO) : Flow<TicketAcquiredDTO>
    fun getTicketsAcquired(dateRangeDTO: DateRangeDTO) :  Flow<TicketAcquiredDTO>
    fun getTransitsByUser(userID : String, dateTimeRangeDTO: DateTimeRangeDTO) : Flow<TransitDTO>
    fun getTransits(dateTimeRangeDTO: DateTimeRangeDTO) : Flow<TransitDTO>

}