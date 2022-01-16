package com.buyer

import com.buyer.dao.IBuyerDao
import com.buyer.modal.Buyer
import com.buyer.modal.Product
import com.buyer.service.BuyerServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class BuyerApplicationTests {

	@Autowired
	private lateinit var buyerServiceImpl: BuyerServiceImpl
	@MockBean
	private lateinit var iBuyerDao: IBuyerDao

	@Test
	fun registerTest(){
		val tempBuyer = Buyer("email@gmail.com","email@gmail.com","password","demoName","demoCity","demoDistrict",
				"demoState",251364,"2563149870", mutableListOf())
		given(iBuyerDao.save(tempBuyer)).willReturn(tempBuyer)
		assert("Successfully Registered" == buyerServiceImpl.register(tempBuyer))
	}

	@Test
	fun loginTest(){
		val tempBuyer = Buyer("example@gmail.com","example@gmail.com","password123","enrique","","",
			"",0,"1256349870", mutableListOf())
		given(iBuyerDao.existsById("example@gmail.com")).willReturn(true)
		given(iBuyerDao.findByEmail("example@gmail.com")).willReturn(tempBuyer)
		assert(buyerServiceImpl.login("example@gmail.com") == tempBuyer)
	}

	@Test
	fun updateBuyerTest(){
		val oldBuyer = Buyer("example@gmail.com","example@gmail.com","password123","enrique","","",
			"",0,"1256349870", mutableListOf())
		val newBuyer = Buyer("example@gmail.com","example@gmail.com","password123","demoName","demoCity","demoDistrict",
			"demoState",251364,"2563149870", mutableListOf())
		given(iBuyerDao.existsById(newBuyer.email)).willReturn(true)
		given(iBuyerDao.findByEmail(newBuyer.email)).willReturn(oldBuyer)
		given(iBuyerDao.save(newBuyer)).willReturn(newBuyer)
		assert(buyerServiceImpl.updateBuyer(newBuyer) != newBuyer)
	}

	@Test
	fun getCart(){
		val tempBuyer = Buyer("example@gmail.com","example@gmail.com","password123","enrique","","",
			"",0,"1256349870", mutableListOf())
		given(iBuyerDao.findByEmail(tempBuyer.email)).willReturn(tempBuyer)
		assert(buyerServiceImpl.getCart(tempBuyer.email) == mutableListOf<Product>())

	}



	@Test
	fun contextLoads() {
	}

}
