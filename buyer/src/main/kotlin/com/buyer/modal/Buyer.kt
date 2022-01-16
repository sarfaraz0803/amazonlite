package com.buyer.modal

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Document("buyerAccount")
data class Buyer(
    @Id
    var _id:String = "",
    @field:Email(message = "Email should be in proper format (example@something.com)")
    @field:NotBlank(message = "email should not be blank")
    @field:Size(min = 8, max = 30, message = "email should be in range of 6-30 chars ")
    var email:String,
    @field:NotBlank(message = "Password should not be blank")
    @field:Size(min = 6, max = 12, message = "Password length must be between 6-12")
    var password:String,
    @field:NotBlank(message = "Name should not be blank")
    var name:String,
    var city:String = "",
    var district:String = "",
    var state:String = "",
    var pinCode:Int = 0,
    @field:NotBlank(message = "Mobile number should not be blank")
    var mobile:String,
    var cart:MutableList<Product> = mutableListOf()
)
