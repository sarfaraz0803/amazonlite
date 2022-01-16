package com.seller.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalException {

    @ExceptionHandler(Warning::class)
    fun idWarning(warning:Warning): ResponseEntity<String?>{
        return ResponseEntity(warning.msg,HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ValidationException::class)
    fun validation(validationException: ValidationException): ResponseEntity<MutableList<String?>> {
        return ResponseEntity(validationException.myMessage,HttpStatus.OK)
    }
}