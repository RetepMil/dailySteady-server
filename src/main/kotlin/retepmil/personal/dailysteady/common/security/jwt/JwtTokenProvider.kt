package retepmil.personal.dailysteady.common.security.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

const val expirationMiliseconds: Long = 1000L * 60L * 60L * 12L // 12시간

const val maxAgeSeconds: Long = 1000L * 60L * 60L * 24L * 14L // 14 일

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    lateinit var secret: String

    private val key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    companion object {
        fun generateRefreshTokenCookie(token: String): ResponseCookie = ResponseCookie.from("refreshToken")
            .value(token)
            .path("/")
            .maxAge(maxAgeSeconds)
            .httpOnly(false) // 배포 환경에서는 true로 설정 필요
            .secure(true)
            .build()
    }

    private val logger: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    /*
     * Token 생성
     */
    fun createToken(authentication: Authentication): TokenInfo {
        logger.debug("Token 생성 시작 : {}", authentication)

        val authorities: String = authentication
            .authorities
            .joinToString(",", transform = GrantedAuthority::getAuthority)
        val accessToken = generateAccessToken(authentication.name, authorities)
        val refreshToken = generateRefreshToken(authentication.name)

        return TokenInfo("Bearer", accessToken, refreshToken).also {
            logger.debug("${authentication.name}에 대한 토큰 발급 정보 :")
            logger.debug("accessToken : {}", it.accessToken)
            logger.debug("refreshToken : {}", it.refreshToken)
        }
    }

    private fun generateAccessToken(name: String, authorities: String): String {
        val now = Date()
        val expireDate = Date(now.time + expirationMiliseconds)
        return Jwts.builder()
            .setSubject(name)
            .claim("auth", authorities)
            .setIssuedAt(now)
            .setExpiration(expireDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    private fun generateRefreshToken(name: String): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(name)
            .setIssuedAt(now)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /*
     * Token 정보 추출
     */
    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token)

        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")

        // 권한 정보 추출
        val authorities: Collection<GrantedAuthority> = (auth as String)
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /*
     * Token 검증
     */
    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: Exception) {
            when(e) {
                is SecurityException -> {} // Invalid JWT Token
                is MalformedJwtException -> {} // Invalid JWT Token
                is ExpiredJwtException -> {} // Expired JWT Token
                is UnsupportedJwtException -> {} // Unsupported JWT Token
                is IllegalArgumentException -> {} // JWT claims string is empty
                else -> {}
            }
            logger.error(e.message)
            false
        }
    }

    private fun getClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
}