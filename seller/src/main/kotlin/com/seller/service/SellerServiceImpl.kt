package com.seller.service

import com.seller.dao.IProductDao
import com.seller.dao.ISellerAccountDao
import com.seller.modal.Product
import com.seller.modal.SellerAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class SellerServiceImpl:IProductService {

    @Autowired
    private lateinit var iProductDao : IProductDao
    @Autowired
    private lateinit var iSellerAccountDao: ISellerAccountDao
    private val passwordEncoder = BCryptPasswordEncoder()

    // =====================================Product_Operations===================================

    override fun addProduct(product: Product): Any {
        val tempPro = iProductDao.productByName(product.name,product.sellerEmail)
        if(tempPro != null){
            return "Product exist"
        }
        iProductDao.save(product)
        return "Successfully Added"
    }

    override fun getAllProducts(): Any {
        return iProductDao.findAll()
    }

    override fun getProductByName(name:String,sellerEmail:String): Product? {
        return iProductDao.productByName(name,sellerEmail)
    }

    override fun deleteProductByName(name: String,sellerEmail: String): String {
        val tempPro = iProductDao.productByName(name,sellerEmail)
        if(tempPro != null){
            tempPro._id?.let { iProductDao.deleteById(it) }
            return "Deleted"
        }
        return "Product Not Exist"
    }

    fun getAllProductsByEmail(email: String):MutableList<Product>{
        val allProducts = iProductDao.findAll()
        val newProList = mutableListOf<Product>()
        for(i in allProducts){
            if(i.sellerEmail == email){
                newProList.add(i)
            }
        }
        return newProList
    }


    //=====================================Seller_Credentials==========================================


    fun registerSeller(sellerAccount: SellerAccount):Any{
        if(!iSellerAccountDao.existsById(sellerAccount.email)){
            val seller = SellerAccount(
                _id = sellerAccount.email,
                email = sellerAccount.email,
                password = passwordEncoder.encode(sellerAccount.password),
                name = sellerAccount.name,
                mobile = sellerAccount.mobile
            )
            iSellerAccountDao.save(seller)
            println(sellerAccount)
            return "Successfully Registered"
        }
        return "Email Already Exist"
    }

    fun login(email: String): SellerAccount? {
        if(iSellerAccountDao.existsById(email)) {
            //println(email)
            return iSellerAccountDao.findByEmail(email)
        }
        return null
    }

    fun updateSeller(sellerAccount: SellerAccount): SellerAccount? {
        if(iSellerAccountDao.existsById(sellerAccount.email)){
            val oldSeller = iSellerAccountDao.findByEmail(sellerAccount.email)
            //println(oldSeller)
            val updatedSeller = SellerAccount(
                    _id = oldSeller._id,
                    email = oldSeller.email,
                    password = passwordEncoder.encode(sellerAccount.password),
                    name = sellerAccount.name,
                    city = sellerAccount.city,
                    district = sellerAccount.district,
                    state = sellerAccount.state,
                    pinCode = sellerAccount.pinCode,
                    mobile = sellerAccount.mobile,
                )
            //println(updatedSeller)
            return iSellerAccountDao.save(updatedSeller)
        }
        return null
    }


}