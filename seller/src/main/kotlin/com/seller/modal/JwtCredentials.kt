package com.seller.modal

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id

@Document("sellerToken")
data class JwtCredentials(
    @Id
    var _id:String,
    var email:String,
    var secretKey:String,
    var token:String
)
