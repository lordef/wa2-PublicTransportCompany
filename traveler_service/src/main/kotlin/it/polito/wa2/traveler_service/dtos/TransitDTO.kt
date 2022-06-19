package it.polito.wa2.traveler_service.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import it.polito.wa2.traveler_service.annotations.ValidTimestamp
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class TransitDTO(
    @field:NotNull
    val timestamp: LocalDateTime?,

    @field:NotBlank(message = "Name cannot be empty or null")
    val username: String,
) {}