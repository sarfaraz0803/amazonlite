package com.buyer.dao

import com.buyer.modal.JwtCredentials
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface IJwtCreDao : MongoRepository<JwtCredentials,String>{

    @Query(value = "{'token':?0}")
    fun credentialFromToken(token:String):JwtCredentials?
}