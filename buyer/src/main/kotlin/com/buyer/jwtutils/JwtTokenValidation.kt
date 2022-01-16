package com.buyer.jwtutils


import com.buyer.dao.IJwtCreDao
import com.buyer.modal.Buyer
import com.buyer.modal.JwtCredentials
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenValidation {

    @Autowired
    private lateinit var iJwtCreDao: IJwtCreDao

    fun generateToken(buyer: Buyer):String{
        val issuer = buyer.email
        val appSecret = buyer.name
        val token = Jwts.builder()
            .setIssuer(issuer)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))   // 1 Day
            .signWith(SignatureAlgorithm.HS512, appSecret)
            .compact()
        val jwtCre = JwtCredentials(buyer._id,buyer.email,appSecret,token)
        iJwtCreDao.save(jwtCre)
        return token
    }

    fun validateUserToken(userId:String, token:String):Any{
        val buyerToken = iJwtCreDao.credentialFromToken(token)
        val buyerFromDb = Jwts.parser().setSigningKey(buyerToken?.secretkey).parseClaimsJws(token).body.issuer
        if(buyerFromDb == userId){
            return true
        }
        return false
    }

    fun deleteJwtCre(delToken: String){
        val delCre = iJwtCreDao.credentialFromToken(delToken)?._id
        if(delCre != null) { iJwtCreDao.deleteById(delCre) }
    }

    fun isTokenExpired(token:String):Boolean{
        val tokenCre = iJwtCreDao.credentialFromToken(token)
        if(tokenCre != null){
            val tokenBody = Jwts.parser().setSigningKey(tokenCre.secretkey).parseClaimsJws(token).body
            return tokenBody.expiration.before(Date(System.currentTimeMillis()))
        }
        return true
    }


}