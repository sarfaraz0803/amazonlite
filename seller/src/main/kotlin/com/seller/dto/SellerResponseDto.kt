package com.seller.dto

import com.seller.modal.SellerAccount

data class SellerResponseDto(
    var account: SellerAccount,
    var token:String
)
