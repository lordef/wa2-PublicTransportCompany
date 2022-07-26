package it.polito.wa2.traveler_service.repositories

import it.polito.wa2.traveler_service.dtos.DateRangeDTO
import it.polito.wa2.traveler_service.entities.TicketAcquired
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface TicketPurchasedRepository : CoroutineCrudRepository<TicketAcquired, Long> {

    fun findAllByUserDetailsUsername(userDetailsUsername : String) : Flow<TicketAcquired>

    suspend fun findTicketAcquiredById(ticketId : Long) : TicketAcquired

    @Query("select ad from TicketAcquired ad where ad.userDetails.username = :#{#username} " +
            "and ad.issuedAt >= :#{#dateRange.from} and ad.issuedAt <= :#{#dateRange.to}")
    fun getTicketsAcquiredByUser(@Param("username") username : String, @Param("dateRange") dateRangeDTO: DateRangeDTO ): Flow<TicketAcquired>


    @Query("select ad from TicketAcquired ad where" +
            " ad.issuedAt >= :#{#dateRange.from} and ad.issuedAt <= :#{#dateRange.to}")
    fun getTicketsAcquired(@Param("dateRange") dateRangeDTO: DateRangeDTO ): Flow<TicketAcquired>

}