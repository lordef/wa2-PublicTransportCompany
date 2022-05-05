package it.polito.wa2.lab3group04.unit_tests

import it.polito.wa2.lab3group04.unit_tests.unit_tests_utils.EmailBaseClass
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class EmailServiceUnitTests() : EmailBaseClass() {

    @Test
    fun wrongEmailFormat(){
        val toMail = "wrongFormatEmail"
        val subject = "Test Subject"
        val mailBody = "Test Body : LOREM IPSUM"

        Assertions.assertEquals(mailService.sendEmail(toMail, subject, mailBody),false)
        /*Assertions.assertThrows(javax.mail.SendFailedException::class.java) {
            mailService.sendEmail(toMail, subject, mailBody)
        }*/
    }

    @Test
    fun emptySubject(){
        val toMail = email
        val subject = ""
        val mailBody = "Test Body : LOREM IPSUM"

        val email = sendAndReceiveEmail{
            mailService.sendEmail(toMail, subject, mailBody)
        }

        Assertions.assertEquals(toMail, email.from, "Wrong email address")
        Assertions.assertEquals(subject, email.subject.trim().replace("\r", ""), "Wrong subject")
        Assertions.assertEquals(mailBody, email.body.trim().replace("\r", ""), "Wrong body")
    }

    @Test
    fun emptyBody(){
        val toMail = email
        val subject = "Test Subject"
        val mailBody = ""

        val email = sendAndReceiveEmail{
            mailService.sendEmail(toMail, subject, mailBody)
        }

        Assertions.assertEquals(toMail, email.from, "Wrong email address")
        Assertions.assertEquals(subject, email.subject.trim().replace("\r", ""), "Wrong subject")
        Assertions.assertEquals(mailBody, email.body.trim().replace("\r", ""), "Wrong body")
    }

    @Test
    fun correctEmail(){
        val toMail = email
        val subject = "Test Subject"
        val mailBody = "Test Body : LOREM IPSUM"

        val email = sendAndReceiveEmail{
            mailService.sendEmail(toMail, subject, mailBody)
        }

        Assertions.assertEquals(toMail, email.from, "Wrong email address")
        Assertions.assertEquals(subject, email.subject.trim().replace("\r", ""), "Wrong subject")
        Assertions.assertEquals(mailBody, email.body.trim().replace("\r", ""), "Wrong body")
    }



}