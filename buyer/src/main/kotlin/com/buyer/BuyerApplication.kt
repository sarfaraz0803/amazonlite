package com.buyer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.web.bind.annotation.CrossOrigin

@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
@CrossOrigin(origins = ["*"])
class BuyerApplication

fun main(args: Array<String>) {
	runApplication<BuyerApplication>(*args)
}
