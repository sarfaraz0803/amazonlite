package com.seller.dao

import com.seller.modal.Product
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IProductDao: MongoRepository<Product, ObjectId> {
    @Query(value = "{'name':?0, 'sellerEmail':?1}")
    fun productByName(name:String,sellerEmail:String):Product?
    /*@Query(value = "{'name':?0, 'sellerEmail':?1}")
    fun existsByName(name: String,sellerEmail: String):Boolean*/
}