package com.seller.service

import com.seller.modal.Product

interface IProductService {
    fun addProduct(product: Product): Any
    fun getAllProducts():Any
    fun getProductByName(name:String,sellerEmail:String):Product?
    //fun updateProductById(product: Product):Any
    fun deleteProductByName(name:String,sellerEmail: String):String
}