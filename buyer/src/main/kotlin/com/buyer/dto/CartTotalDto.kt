package com.buyer.dto

import com.buyer.modal.Product

data class CartTotalDto(
    val cartTotal:Double,
    val items:Int,
    val productList:MutableList<Product>
)
