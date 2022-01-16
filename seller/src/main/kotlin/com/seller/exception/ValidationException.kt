package com.seller.exception

data class ValidationException(
    var myMessage :MutableList<String?>
):RuntimeException()
