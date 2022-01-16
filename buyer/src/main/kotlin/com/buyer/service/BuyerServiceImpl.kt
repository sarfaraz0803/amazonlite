package com.buyer.service

import com.buyer.client.BuyerClient
import com.buyer.dao.IBuyerDao
import com.buyer.dto.LoginDto
import com.buyer.dto.OrderDto
import com.buyer.modal.Buyer
import com.buyer.modal.Product
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class BuyerServiceImpl {

    @Autowired
    private lateinit var iBuyerDao: IBuyerDao
    @Autowired
    private lateinit var buyerClient: BuyerClient
    @Autowired
    private lateinit var mailSender: JavaMailSender
    private val passwordEncoder = BCryptPasswordEncoder()

    fun register(buyer:Buyer):Any{
        if(!iBuyerDao.existsById(buyer.email)){
            val cusBuyer = Buyer(
                _id = buyer.email,
                email = buyer.email,
                password = passwordEncoder.encode(buyer.password),
                name = buyer.name,
                mobile = buyer.mobile
            )
            iBuyerDao.save(cusBuyer)
            //println(buyer)
            return "Successfully Registered"
        }
        return "This email already registered"
    }

    fun login(email: String): Buyer? {
        if(iBuyerDao.existsById(email)){
            //println(email)
            return iBuyerDao.findByEmail(email)
        }
        return null
    }

    fun updateBuyer(buyer: Buyer): Buyer? {
        if(iBuyerDao.existsById(buyer.email)){
            val oldBuyer = iBuyerDao.findByEmail(buyer.email)
            //println("oldBuyer : $oldBuyer")
            val updatedBuyer = Buyer(
                _id = oldBuyer._id,
                email = oldBuyer.email,
                password = passwordEncoder.encode(buyer.password),
                name = buyer.name,
                city = buyer.city,
                district = buyer.district,
                state = buyer.state,
                pinCode = buyer.pinCode,
                mobile = buyer.mobile,
                cart = oldBuyer.cart
            )
            //println("newBuyer : $updatedBuyer")
            return iBuyerDao.save(updatedBuyer)
        }
        return null
    }

    fun getCart(email:String):MutableList<Product>{
        //println(email)
        return iBuyerDao.findByEmail(email).cart
    }

    fun addProCart(email: String, pName: String, pSellerEmail:String):Any{
        val proList = mutableListOf<Product>()
        val reqPro = buyerClient.getProduct(pName,pSellerEmail)
        if(iBuyerDao.existsById(email)){
            if(reqPro != null){
                val buyerAcc = iBuyerDao.findByEmail(email)
                if(buyerAcc.cart.isNotEmpty()) {
                    for (i in buyerAcc.cart) {
                        proList.add(i)
                    }
                }
                proList.add(reqPro)
                buyerAcc.cart = proList
                iBuyerDao.save(buyerAcc)
                return "Product Added"
            }
            return "Product not exist"
        }
        return "Email Not Exist"
    }

    fun delProCart(email: String, pName: String, pSellerEmail:String):Any{
        val proList = mutableListOf<Product>()
        var flag = 0
        if(iBuyerDao.existsById(email)){
            val buyerAcc = iBuyerDao.findByEmail(email)
            if(buyerAcc.cart.isNotEmpty()) {
                for (i in buyerAcc.cart) {
                    if(i.name != pName && i.sellerEmail != pSellerEmail){
                        proList.add(i)
                    }else{
                        flag+=1
                    }
                }
            }
            buyerAcc.cart = proList
            iBuyerDao.save(buyerAcc)
            if(flag != 0){
                return "Product Deleted"
            }
            return "No Product in cart with such name"
        }
        return "Email Not Exist"

    }

    fun checkout(buyerEmail:String):Any{
        val sellerEmails = mutableListOf<String>()
        val orderList = mutableMapOf<String,OrderDto>()
        if(iBuyerDao.existsById(buyerEmail)) {
            val buyerAcc = iBuyerDao.findByEmail(buyerEmail)
            for (i in buyerAcc.cart) {
                if(!sellerEmails.contains(i.sellerEmail)){sellerEmails.add(i.sellerEmail)}
            }
            for (j in sellerEmails){
                val pros = mutableListOf<Product>()
                for(k in buyerAcc.cart){
                    if(k.sellerEmail == j){
                        pros.add(k)
                    }
                }
                val finalProductsList = pros.distinctBy { pro -> pro.name } as MutableList<Product>
                val orderPlaced = OrderDto(ObjectId(),buyerEmail,buyerAcc.name,buyerAcc.city,buyerAcc.district,
                    buyerAcc.state,buyerAcc.pinCode,buyerAcc.mobile,finalProductsList)
                //sendSimpleEmail("punchman030@gmail.com","Your order",orderPlaced.toString())
                orderList[j] = orderPlaced
            }
            return orderList
        }
        return "Buyer Not Exist"
    }


    //================================Email_Service============================

    fun sendSimpleEmail(toEmail: String, subject: String,body: String) {
        val mess = SimpleMailMessage()
        mess.setFrom("homaster4595@gmail.com")
        mess.setTo(toEmail)
        mess.setSubject(subject)
        mess.setText(body)
        mailSender.send(mess)
        //println(mess.toString())
    }

}