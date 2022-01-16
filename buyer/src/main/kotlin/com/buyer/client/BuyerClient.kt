package com.buyer.client

import com.buyer.modal.Product
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(url="http://localhost:8762/seller", name="buyerClient")
interface BuyerClient {

    @GetMapping("/allProducts")
    fun allProducts():Any

    @GetMapping("/productByName/{name}/{sellerEmail}")
    fun getProduct(@PathVariable name:String,@PathVariable sellerEmail:String): Product?
}