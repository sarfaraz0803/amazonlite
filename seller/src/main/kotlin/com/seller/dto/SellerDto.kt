package com.seller.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class SellerDto(
    @field:Email(message = "Email should be in proper format (example@something.com)")
    @field:NotBlank(message = "email should not be blank")
    @field:Size(min = 8, max = 30, message = "email should be in range of 6-30 chars ")
    var email:String,
    @field:NotBlank(message = "Password should not be blank")
    @field:Size(min = 6, max = 12, message = "Password length must be between 6-12")
    var password:String
)
