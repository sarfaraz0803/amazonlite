package com.seller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class SellerApplication

fun main(args: Array<String>) {
	runApplication<SellerApplication>(*args)
}
