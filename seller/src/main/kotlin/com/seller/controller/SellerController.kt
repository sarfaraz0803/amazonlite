package com.seller.controller


import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.seller.dao.IJwtCreDao
import com.seller.dto.SellerDto
import com.seller.dto.SellerResponseDto
import com.seller.jwtutils.JwtTokenValidation
import com.seller.modal.Product
import com.seller.modal.SellerAccount
import com.seller.service.SellerServiceImpl
import io.jsonwebtoken.ExpiredJwtException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.ws.rs.PUT
import javax.ws.rs.Path

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/seller")
class SellerController {

    @Autowired
    private lateinit var sellerServiceImpl : SellerServiceImpl
    @Autowired
    private lateinit var jwtTokenValidation:JwtTokenValidation
    @Autowired
    private lateinit var iJwtCreDao: IJwtCreDao
    private val passwordEncoder = BCryptPasswordEncoder()


    private var amazonS3client: AmazonS3? =null

    @Value("\${amazon.s3.bucket.name}")
    private val bucketName: String? = null


    init {
        val credentials: AWSCredentials = BasicAWSCredentials("your_accesskey", "your_secretkey")
        amazonS3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_SOUTH_1)
            .build()
    }


    // =====================================Product_Operations===================================

    /*@PostMapping("/addProduct")
    fun addProduct(request: HttpServletRequest,
                   @RequestParam("Image", required = false) imageFile: MultipartFile,
                   @RequestParam("name") name: String,
                   @RequestParam("price") price:Double,
                   @RequestParam("category") category:String,
                   @RequestParam("description") description:String
    ):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        try {
                            if(!file.isEmpty && product.isNotEmpty()){
                                val fileType = arrayOf("image/png","image/jpg","image/jpeg")
                                if(file.contentType in fileType){
                                    val proImage = File("G:\\amazonLite\\"+file.originalFilename)
                                    proImage.createNewFile()
                                    val fos = FileOutputStream(proImage)
                                    fos.write(file.bytes)
                                    fos.close()
                                    val customPro:Product = ObjectMapper().readValue(product)
                                    val finalProduct = Product(ObjectId(),userId,customPro.name,customPro.price,customPro.category,customPro.description,proImage.toString())
                                    return ResponseEntity(sellerServiceImpl.addProduct(finalProduct), HttpStatus.OK)
                                }
                                return ResponseEntity("Image should be in jpg or png format",HttpStatus.OK)
                            }
                            return ResponseEntity("Image or Product should not be Empty",HttpStatus.OK)

                        }catch (e:Exception) {
                            return ResponseEntity(e.message, HttpStatus.OK)
                        }
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
    }*/

    @PostMapping("/addProduct")
    fun addProduct(request: HttpServletRequest,
                   @RequestParam("Image", required = false) imageFile: MultipartFile,
                   @RequestParam("name") name: String,
                   @RequestParam("price") price:Double,
                   @RequestParam("category") category:String,
                   @RequestParam("description") description:String
    ):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        val imageFileName = imageFile.originalFilename.toString()
                        val file = File(imageFileName)
                        val fileName = UUID.randomUUID().toString() + "." + imageFile.originalFilename
                        amazonS3client?.putObject(PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead))
                        val finalImage = amazonS3client?.getUrl(bucketName, fileName).toString()
                        val finalProduct = Product(ObjectId(),userId,name,price,category,description,finalImage)
                        return ResponseEntity(sellerServiceImpl.addProduct(finalProduct), HttpStatus.OK)
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
    fun getAllProducts():ResponseEntity<Any>{
        return ResponseEntity(sellerServiceImpl.getAllProducts(),HttpStatus.OK)
    }

    @GetMapping("/productByName/{name}/{sellerEmail}")
    fun getProduct(@PathVariable name:String,@PathVariable sellerEmail: String):ResponseEntity<Any>{
        return ResponseEntity(sellerServiceImpl.getProductByName(name,sellerEmail), HttpStatus.OK)
    }

    @DeleteMapping("/delete/{name}/{sellerEmail}")
    fun deleteProduct(request: HttpServletRequest,@PathVariable name:String,@PathVariable sellerEmail:String):ResponseEntity<String>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(sellerServiceImpl.deleteProductByName(name,sellerEmail),HttpStatus.OK)
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

    @GetMapping("/myProducts")
    fun allProductsByEmail(request: HttpServletRequest):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        val products = sellerServiceImpl.getAllProductsByEmail(userId)
                        if(products.isEmpty()){
                            return ResponseEntity("No Products",HttpStatus.OK)
                        }
                        return ResponseEntity(products,HttpStatus.OK)
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

    @GetMapping("/productsByName/{name}")
    fun getProductsByName(@PathVariable name:String):ResponseEntity<Any>{
        val proResponse = sellerServiceImpl.getProByName(name)
        if (proResponse.isNotEmpty()){
            return ResponseEntity(proResponse,HttpStatus.OK)
        }
        return ResponseEntity("No Product",HttpStatus.OK)
    }


    //=====================================Seller_Credentials==========================================


    @PostMapping("/register")
    fun registerSeller(@Valid @RequestBody sellerAccount: SellerAccount, bindingResults: BindingResult):ResponseEntity<Any>{
        if(bindingResults.hasErrors()){
            val errors: List<FieldError> = bindingResults.fieldErrors
            val errorList: MutableList<String?> = mutableListOf()
            for (er in errors){ errorList.add(er.defaultMessage) }
            //throw ValidationException(errorList.asReversed())
            return ResponseEntity(errorList,HttpStatus.OK)
        }
        val seller = sellerServiceImpl.registerSeller(sellerAccount)
        return ResponseEntity(seller,HttpStatus.OK)

    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody sellerDto: SellerDto, bindingResults: BindingResult, response:HttpServletResponse):ResponseEntity<Any>{
        if(bindingResults.hasErrors()){
            val errors: List<FieldError> = bindingResults.fieldErrors
            val errorList: MutableList<String?> = mutableListOf()
            for (er in errors){ errorList.add(er.defaultMessage) }
            //throw ValidationException(errorList.asReversed())
            return ResponseEntity(errorList,HttpStatus.OK)
        }
        val logSeller = sellerServiceImpl.login(sellerDto.email)
        if(logSeller != null){
            if(passwordEncoder.matches(sellerDto.password,logSeller.password)){
                val token = jwtTokenValidation.generateToken(logSeller)
                response.addHeader("email",logSeller.email)
                response.addHeader("Authorization",token)
                val sellerResponse = SellerResponseDto(
                    email = logSeller.email,
                    token = token
                )

                return ResponseEntity(sellerResponse,HttpStatus.OK)
            }
            return ResponseEntity("wrong password",HttpStatus.OK)
        }
        return ResponseEntity("email not registered",HttpStatus.OK)

    }

    @GetMapping("/loggedSeller")
    fun loggedSeller(request: HttpServletRequest):ResponseEntity<Any>{
        val userId = request.getHeader("email")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        val logSeller = sellerServiceImpl.login(userId)
                        if(logSeller != null){
                            return ResponseEntity(logSeller,HttpStatus.OK)
                        }
                        return ResponseEntity("Email Not Exist",HttpStatus.OK)
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

    @PutMapping("/update")
    fun updateSeller(request: HttpServletRequest, @Valid @RequestBody sellerAccount: SellerAccount, bindingResults: BindingResult):ResponseEntity<Any>{
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
                        val updatedAccount = sellerServiceImpl.updateSeller(sellerAccount)
                        if(updatedAccount!=null){
                            return ResponseEntity("Successfully Updated",HttpStatus.OK)
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



}