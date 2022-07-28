package it.polito.wa2.traveler_service.controllers


import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import it.polito.wa2.traveler_service.dtos.*
import it.polito.wa2.traveler_service.exceptions.BadRequestException
import it.polito.wa2.traveler_service.services.AdminReportsService
import it.polito.wa2.traveler_service.services.impl.AdminReportsServiceImpl
import it.polito.wa2.traveler_service.services.impl.TransitServiceImpl
import it.polito.wa2.traveler_service.services.impl.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid


@RestController
class TravelerController {

    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    @Autowired
    lateinit var transitService: TransitServiceImpl

    @Autowired
    lateinit var adminReportsService: AdminReportsServiceImpl

    @GetMapping("/my/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    fun getMyProfile(): GetMyProfileResponseBody {

        val userName = SecurityContextHolder.getContext().authentication.name
        val userDetailsDTO = userDetailsService.getUserProfile(userName)

        return GetMyProfileResponseBody(
            userDetailsDTO.name,
            userDetailsDTO.address,
            userDetailsDTO.telephone_number,
            userDetailsDTO.date_of_birth
        )
    }

    @PutMapping("/my/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    fun putMyProfile(
        @RequestBody @Valid userDetailsDTO: UserDetailsDTO,
        bindingResult: BindingResult
    ) {
        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        //if date not null and not empty , the only valid format is dd-MM-yyyy
        if ((userDetailsDTO.date_of_birth != null && userDetailsDTO.date_of_birth != "") && !validDate(userDetailsDTO.date_of_birth as String))
            throw BadRequestException("Wrong json date field")


        //adding username to the DTO
        userDetailsDTO.username = SecurityContextHolder.getContext().authentication.name
        //putting user info in the db
        userDetailsService.putUserProfile(userDetailsDTO)

    }

    @GetMapping("/my/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    @ResponseBody
    fun getMyTickets(): List<TicketAcquiredDTO> {
        return userDetailsService.getUserTickets(SecurityContextHolder.getContext().authentication.name)
    }

    @PostMapping("/my/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).SERVICE)")
    @ResponseBody
    fun postMyTickets(
        @RequestBody @Valid purchaseTicketDTO: PurchaseTicketDTO,
        bindingResult: BindingResult
    )/*: List<TicketAcquiredDTO> */{
        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        println(purchaseTicketDTO)

        //posting tickets in the db
        userDetailsService.postUserTickets(
            SecurityContextHolder.getContext().authentication.name,
            purchaseTicketDTO
        )
    }

    @GetMapping(path = ["/single-ticket"], produces = [MediaType.IMAGE_PNG_VALUE])
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).CUSTOMER)")
    fun generateQRCodeImage(@RequestParam ticketId: String): ByteArray? {

        val userName = SecurityContextHolder.getContext().authentication.name

        //QRcode generator logic
        val qrCodeWriter = QRCodeWriter()
        val ticketDTO = userDetailsService.getTicketById(ticketId.toLong(), userName)
        val bitMatrix: BitMatrix = qrCodeWriter.encode(ticketDTO.jws, BarcodeFormat.QR_CODE, 250, 250)

        val pngOutputStream = ByteArrayOutputStream()
        val con = MatrixToImageConfig(-0xfffffe, -0x3fbf)

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con)
        val pngData: ByteArray = pngOutputStream.toByteArray()

        return pngData
    }

    @GetMapping("/admin/travelers")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getAdminTravelers(): List<String> {
        return userDetailsService.getTravelers()
    }

    @GetMapping("/admin/traveler/{userID}/profile")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getAdminProfile(@PathVariable("userID") userID: String): UserDetailsDTO {
        return userDetailsService.getUserProfile(userID)
    }

    @GetMapping("/admin/traveler/{userID}/tickets")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getAdminTickets(@PathVariable("userID") userID: String): List<TicketAcquiredDTO> {
        return userDetailsService.getUserTickets(userID)
    }



    /** APIs for statistics about purchases and transits  **/

    @GetMapping("/admin/report/{userID}/purchases")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getPurchasesByUser(@PathVariable("userID") userID: String,
                           @RequestBody @Valid dateRangeDTO: DateRangeDTO,
                           bindingResult: BindingResult): List<TicketAcquiredDTO> {

        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")
        println(dateRangeDTO)
        return adminReportsService.getTicketsAcquiredByUser(userID, dateRangeDTO)
    }

    @GetMapping("/admin/report/{userID}/transits")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getTransitsByUser(@PathVariable("userID") userID: String,
                          @RequestBody @Valid dateTimeRangeDTO: DateTimeRangeDTO,
                          bindingResult: BindingResult): List<TransitDTO> {

        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")


        return adminReportsService.getTransitsByUser(userID, dateTimeRangeDTO)
    }

    @GetMapping("/admin/report/purchases")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getPurchases( @RequestBody @Valid dateRangeDTO: DateRangeDTO,
                      bindingResult: BindingResult): List<TicketAcquiredDTO> {

        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")


        return adminReportsService.getTicketsAcquired(dateRangeDTO)
    }
    @GetMapping("/admin/report/transits")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getTransits( @RequestBody @Valid dateTimeRangeDTO: DateTimeRangeDTO,
                     bindingResult: BindingResult): List<TransitDTO> {

        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")


        return adminReportsService.getTransits(dateTimeRangeDTO)
    }

    @GetMapping("/embedded/secret")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).ADMIN)")
    @ResponseBody
    fun getSecret( @RequestBody @Valid dateTimeRangeDTO: DateTimeRangeDTO,
                   bindingResult: BindingResult): List<TransitDTO> {

        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")


        return adminReportsService.getTransits(dateTimeRangeDTO)
    }


    @PostMapping("/transit")
    @PreAuthorize("hasAuthority(T(it.polito.wa2.traveler_service.dtos.Role).EMBEDDED_SYSTEM)")
    @ResponseBody
    fun postTransit(
        @RequestBody @Valid transitDTO: TransitDTO,
        bindingResult: BindingResult
    ) {
        if (bindingResult.hasErrors())
            throw BadRequestException("Wrong json fields")

        //putting transit info in the db
        transitService.postTransit(transitDTO)
    }

    //the only valid format is dd-MM-yyyy
    private fun validDate(date: String): Boolean {
        val stringParsed = date.split("-")
        val day = stringParsed.get(0).toInt()
        val month = stringParsed.get(1).toInt()
        val year = stringParsed.get(2).substring(2, 4).toInt()
        val firstCheck = when (month) {
            //if February
            2 -> {
                if (year % 4 == 0) {//se bisestile (if leap)
                    if (day > 29)
                        false
                    else true
                } else {
                    if (day > 28)
                        false
                    else true
                }

            }
            //if April
            4 -> {
                if (day > 30)
                    false
                else true
            }
            //if June
            6 -> {
                if (day > 30)
                    false
                else true
            }
            //if September
            9 -> {
                if (day > 30)
                    false
                else true
            }
            //if November
            11 -> {
                if (day > 30)
                    false
                else true
            }
            else -> true
        }
        if (firstCheck && notFutureDate(date))
            return true
        else return false
    }

    private fun notFutureDate(date: String): Boolean {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(date)

        if (date.compareTo(formatter.parse(formatter.format(Date()))) <= 0)
            return true
        else return false
    }

}

data class GetMyProfileResponseBody(
    val name: String?,
    val address: String?,
    val telephone_number: String?,
    val date_of_birth: String?
) {}
