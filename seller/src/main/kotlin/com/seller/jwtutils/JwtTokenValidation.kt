package com.seller.jwtutils


import com.seller.dao.IJwtCreDao
import com.seller.modal.JwtCredentials
import com.seller.modal.SellerAccount
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenValidation {

    @Autowired
    private lateinit var iJwtCreDao: IJwtCreDao


    fun generateToken(sellerAccount: SellerAccount):String{
        val issuer = sellerAccount.email
        val appSecret = sellerAccount.name
        val token = Jwts.builder()
            .setIssuer(issuer)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))   // 1 Day
            .signWith(SignatureAlgorithm.HS512, appSecret)
            .compact()
        val jwtCre = JwtCredentials(sellerAccount._id,sellerAccount.email,appSecret,token)
        iJwtCreDao.save(jwtCre)
        return token
    }

    fun validateUserToken(email:String, token:String):Any{
        val getJwtCre = iJwtCreDao.findById(email)
        if(getJwtCre != null){
            val tokenIssuerFromDb = Jwts.parser().setSigningKey(getJwtCre.get().secretKey).parseClaimsJws(getJwtCre.get().token).body.issuer
            val tokenIssuerFromRequest = iJwtCreDao.credentialFromToken(token)?._id
            if(tokenIssuerFromDb == tokenIssuerFromRequest){
                return true
            }
            return false
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
            val tokenBody = Jwts.parser().setSigningKey(tokenCre.secretKey).parseClaimsJws(token).body
            return tokenBody.expiration.before(Date(System.currentTimeMillis()))
        }
        return true
    }


}