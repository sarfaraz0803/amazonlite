package com.seller.exception


data class Warning (
    val msg: String?
    ): RuntimeException()