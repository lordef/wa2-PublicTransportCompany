package it.polito.wa2.traveler_service.repositories


import it.polito.wa2.traveler_service.dtos.DateTimeRangeDTO
import it.polito.wa2.traveler_service.entities.Transit
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface TransitRepository : CoroutineCrudRepository<Transit, Long> {

    @Query("select t from Transit t where t.userDetails.username = :#{#username} " +
            "and t.timestamp >= :#{#dateRange.from} and t.timestamp <= :#{#dateRange.to}")
    fun getTransitsByUser(@Param("username") username : String, @Param("dateRange") dateTimeRangeDTO: DateTimeRangeDTO): Flow<Transit>

    @Query("select t from Transit t where " +
            "t.timestamp >= :#{#dateRange.from} and t.timestamp <= :#{#dateRange.to}")
    fun getTransits(@Param("dateRange") dateTimeRangeDTO: DateTimeRangeDTO ): Flow<Transit>
}