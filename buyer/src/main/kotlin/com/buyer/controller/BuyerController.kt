package com.buyer.controller

import com.buyer.client.BuyerClient
import com.buyer.dao.IJwtCreDao
import com.buyer.dto.BuyerResponseDto
import com.buyer.dto.LoginDto
import com.buyer.dto.MessageDto
import com.buyer.jwtutils.JwtTokenValidation
import com.buyer.modal.Buyer
import com.buyer.modal.Product
import com.buyer.service.BuyerServiceImpl
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.ws.rs.Path

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/buyer")
class BuyerController {

    @Autowired
    private lateinit var buyerServiceImpl: BuyerServiceImpl
    @Autowired
    private lateinit var jwtTokenValidation: JwtTokenValidation
    @Autowired
    private lateinit var iJwtCreDao: IJwtCreDao
    @Autowired
    private lateinit var buyerClient: BuyerClient
    private val passwordEncoder = BCryptPasswordEncoder()


    @PostMapping("/register")
    fun register(@Valid @RequestBody buyer: Buyer, bindingResults: BindingResult):ResponseEntity<Any>{
        if(bindingResults.hasErrors()){
            val errors: List<FieldError> = bindingResults.fieldErrors
            val errorList: MutableList<String?> = mutableListOf()
            for (er in errors){ errorList.add(er.defaultMessage) }
            return ResponseEntity(errorList.asReversed(),HttpStatus.OK)
        }
        return ResponseEntity(buyerServiceImpl.register(buyer),HttpStatus.OK)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginDto: LoginDto, bindingResults: BindingResult,response: HttpServletResponse):ResponseEntity<Any>{
        if(bindingResults.hasErrors()){
            val errors: List<FieldError> = bindingResults.fieldErrors
            val errorList: MutableList<String?> = mutableListOf()
            for (er in errors){ errorList.add(er.defaultMessage) }
            //throw ValidationException(errorList.asReversed())
            return ResponseEntity(errorList.asReversed(),HttpStatus.OK)
        }
        val loggedBuyer = buyerServiceImpl.login(loginDto.email)
        if(loggedBuyer != null){
            if(passwordEncoder.matches(loginDto.password,loggedBuyer.password)){
                val token = jwtTokenValidation.generateToken(loggedBuyer)
                val buyerResponse = BuyerResponseDto(
                    account = loggedBuyer,
                    token = token
                )
                return ResponseEntity(buyerResponse,HttpStatus.OK)
            }
            return ResponseEntity("Wrong Password",HttpStatus.OK)
        }
        return ResponseEntity("Buyer/Email Not Exist",HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        iJwtCreDao.deleteById(userId)
                        return ResponseEntity("Successfully LoggedOut",HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e: ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! Email Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & email",HttpStatus.OK)
    }

    @GetMapping("/allProducts")
    fun getAllProducts(request: HttpServletRequest):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(buyerClient.allProducts(),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e: ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! Email Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & email",HttpStatus.OK)
    }

    @PutMapping("/update")
    fun updateBuyer(request: HttpServletRequest, @Valid @RequestBody buyer: Buyer, bindingResults: BindingResult):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        if(bindingResults.hasErrors()){
                            val errors: List<FieldError> = bindingResults.fieldErrors
                            val errorList: MutableList<String?> = mutableListOf()
                            for (er in errors){ errorList.add(er.defaultMessage) }
                            //throw ValidationException(errorList.asReversed())
                            return ResponseEntity(errorList,HttpStatus.OK)
                        }
                        val updatedAccount = buyerServiceImpl.updateBuyer(buyer)
                        if(updatedAccount!=null){
                            return ResponseEntity(updatedAccount,HttpStatus.OK)
                        }
                        return ResponseEntity("Account Not Exist",HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e: ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! Email Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & email",HttpStatus.OK)
    }

    @GetMapping("/cart")
    fun getCart(request: HttpServletRequest):ResponseEntity<Any> {
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(buyerServiceImpl.getCart(userId),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e: ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! Email Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & email",HttpStatus.OK)
    }

    @PutMapping("/addToCart/{name}/{sellerEmail}")
    fun addProductCart(request: HttpServletRequest,@PathVariable name:String, @PathVariable sellerEmail: String):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(buyerServiceImpl.addProCart(userId,name,sellerEmail),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e: ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! Email Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & email",HttpStatus.OK)
    }

    @PutMapping("/delFromCart/{name}/{sellerEmail}")
    fun deleteProductFromCart(request: HttpServletRequest,@PathVariable name:String, @PathVariable sellerEmail: String):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(buyerServiceImpl.delProCart(userId,name,sellerEmail),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e: ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! Email Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & email",HttpStatus.OK)
    }

    @GetMapping("/checkout")
    fun checkout(request: HttpServletRequest):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(buyerServiceImpl.checkout(userId),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e: ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! Email Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & email",HttpStatus.OK)

    }


}