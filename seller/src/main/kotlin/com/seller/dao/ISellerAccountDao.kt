package com.seller.dao

import com.seller.modal.SellerAccount
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ISellerAccountDao:MongoRepository<SellerAccount, String> {
    fun findByEmail(email:String):SellerAccount
}