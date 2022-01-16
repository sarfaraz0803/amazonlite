package com.buyer.dao

import com.buyer.modal.Buyer
import org.springframework.data.mongodb.repository.MongoRepository

interface IBuyerDao: MongoRepository<Buyer, String> {
    fun findByEmail(email:String):Buyer
}