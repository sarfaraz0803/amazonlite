package com.seller.modal

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("product")
data class Product(
    @Id
    var _id:ObjectId? = null,
    var sellerEmail:String = "",
    var name:String = "",
    var price:Double = 0.0,
    var category:String = "",
    var description:String = "",
    var image:String = ""
)
