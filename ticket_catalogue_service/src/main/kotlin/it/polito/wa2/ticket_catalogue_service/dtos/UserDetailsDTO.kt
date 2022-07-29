package it.polito.wa2.ticket_catalogue_service.dtos

data class UserDetailsDTO(
    var orderId: Long,
    var username: String,
    var name: String?,
    var address: String?,
    var telephone_number: String?,
    var date_of_birth: String?
)
