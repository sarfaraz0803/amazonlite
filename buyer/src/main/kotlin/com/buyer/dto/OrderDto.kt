package com.buyer.dto

import com.buyer.modal.Product
import org.bson.types.ObjectId

data class OrderDto(
    var orderId:ObjectId,
    var bEmail:String = "",
    var bName:String = "",
    var bCity:String = "",
    var bDistrict:String = "",
    var bState:String = "",
    var bPinCode:Int = 0,
    var bMobile:String = "",
    var productList: MutableList<Product> = mutableListOf()
)
