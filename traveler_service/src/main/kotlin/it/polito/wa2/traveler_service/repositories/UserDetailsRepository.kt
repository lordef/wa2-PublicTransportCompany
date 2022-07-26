package it.polito.wa2.traveler_service.repositories

import it.polito.wa2.traveler_service.entities.UserDetails
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository




@Repository
interface UserDetailsRepository : CoroutineCrudRepository<UserDetails, Long> {

    suspend fun findByUsername(username: String): UserDetails?//findByUsername(nickname: String): UserDetails?

    @Query("select ud.username from UserDetails ud")
    fun findUsernames(): Flow<String>

}