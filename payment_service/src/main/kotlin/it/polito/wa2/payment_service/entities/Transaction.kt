package it.polito.wa2.payment_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("transactions")
data class Transaction (
    @Id
    val transactionId: Long?=null,
    val amount: Float,
    val customer: String,
    val orderId: Long?=null,
    val date: Date,
    var status: Status = Status.PENDING,
    val cardHolder: String

        )