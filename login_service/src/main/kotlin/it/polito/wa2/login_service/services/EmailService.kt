package it.polito.wa2.lab3group04.services

interface EmailService {
    fun sendEmail(toMail: String, subject: String, body: String): Boolean
}