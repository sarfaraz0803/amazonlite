package com.buyer.dto

import com.buyer.modal.Buyer

data class BuyerResponseDto(
    var account: Buyer,
    var token:String
)
