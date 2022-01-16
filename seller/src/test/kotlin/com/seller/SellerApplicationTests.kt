package com.seller

import com.seller.dao.ISellerAccountDao
import com.seller.modal.SellerAccount
import com.seller.service.SellerServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class SellerApplicationTests {

	@Autowired
	private lateinit var sellerServiceImpl: SellerServiceImpl
	@MockBean
	private lateinit var iSellerAccountDao: ISellerAccountDao

	@Test
	fun registerSellerTest(){
		val tempAccount = SellerAccount("something@gmail.com", "something@gmail.com","password123","suliamaan",
		"","","",0,"12365489870")
		given(iSellerAccountDao.existsById(tempAccount.email)).willReturn(false)
		given(iSellerAccountDao.save(tempAccount)).willReturn(tempAccount)
		assert(sellerServiceImpl.registerSeller(tempAccount) == "Successfully Registered")
	}

	@Test
	fun loginSeller(){
		val tempAccount = SellerAccount("something@gmail.com", "something@gmail.com","password123","suliamaan",
			"","","",0,"12365489870")
		given(iSellerAccountDao.existsById(tempAccount.email)).willReturn(true)
		given(iSellerAccountDao.findByEmail(tempAccount.email)).willReturn(tempAccount)
		assert(sellerServiceImpl.login(tempAccount.email) == tempAccount)
	}

	@Test
	fun updateSellerTest(){
		val oldSelAcc = SellerAccount("something@gmail.com", "something@gmail.com","password123","suliamaan",
			"","","",0,"12365489870")
		val newSelAcc= SellerAccount("something@gmail.com", "something@gmail.com","password123","suliamaan yusuf",
			"bijnor","bijnor","uttarpradesh",246701,"1258989970")
		given(iSellerAccountDao.existsById(newSelAcc.email)).willReturn(true)
		given(iSellerAccountDao.findByEmail(newSelAcc.email)).willReturn(oldSelAcc)
		given(iSellerAccountDao.save(newSelAcc)).willReturn(newSelAcc)
		assert(sellerServiceImpl.updateSeller(newSelAcc) != newSelAcc)
	}


	@Test
	fun contextLoads() {
	}

}
