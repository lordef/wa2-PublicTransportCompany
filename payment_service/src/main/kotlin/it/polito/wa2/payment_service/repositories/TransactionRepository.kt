package it.polito.wa2.payment_service.repositories

import it.polito.wa2.payment_service.entities.Transaction
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository: CoroutineCrudRepository<Transaction,Long> {
}