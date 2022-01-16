package com.buyer.modal

import org.bson.types.ObjectId

data class Product(
    var _id:ObjectId? = null,
    var sellerEmail:String = "",
    var name:String = "",
    var price:Double = 0.0,
    var category:String = "",
    var description:String = "",
    var image:String = ""
)
