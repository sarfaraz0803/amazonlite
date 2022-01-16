package com.buyer.modal

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id

@Document("buyerToken")
data class JwtCredentials(
    @Id
    var _id:String,
    var email:String,
    var secretkey:String,
    var token:String
)
