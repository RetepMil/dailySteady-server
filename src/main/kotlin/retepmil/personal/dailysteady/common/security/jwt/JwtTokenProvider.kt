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
import retepmil.personal.dailysteady.common.security.exception.InvalidTokenException
import retepmil.personal.dailysteady.common.security.exception.RefreshTokenNotFoundException
import retepmil.personal.dailysteady.common.security.repository.MemberRoleRepository
import retepmil.personal.dailysteady.common.security.repository.RefreshTokenRepository
import retepmil.personal.dailysteady.members.dto.MemberLoginResponseDto
import retepmil.personal.dailysteady.members.exception.MemberNotFoundException
import retepmil.personal.dailysteady.members.repository.MemberRepository
import java.util.*
import javax.security.auth.RefreshFailedException

const val ACCESS_EXPIRATION_MILLISECOND: Long = 1000L * 60L * 60L * 24L // 24 HOURS
const val REFRESH_EXPIRATION_MILLISECOND: Long = 1000L * 60L * 60L * 24L * 7L // 7 DAYS

@Component
class JwtTokenProvider(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
) {

    @Value("\${jwt.secret}")
    lateinit var secret: String

    private val key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    private val logger: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

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

    fun renewToken(accessToken: String, refreshToken: String): MemberLoginResponseDto {
        logger.debug("Token Renew 로직 시작")

        if (validateToken(accessToken) != JwtCode.EXPIRED)
            throw InvalidTokenException("Access Token 값이 EXPIRED 상태가 아닙니다")
        if (validateToken(refreshToken) != JwtCode.EXPIRED)
            throw InvalidTokenException("Refresh Token 값이 EXPIRED 상태가 아닙니다")

        val email = getClaims(accessToken).subject
        val member = memberRepository.findByEmail(email) ?: throw MemberNotFoundException()

        val storedRefreshToken = refreshTokenRepository.findByEmail(email)
            ?: throw RefreshTokenNotFoundException()
        if (refreshToken != storedRefreshToken.refreshTokenValue)
            throw RefreshFailedException("전달받은 Refresh Token 값이 DB에 저장되어 있는 값과 상이합니다")

        val memberRole = memberRoleRepository.findByMemberId(member.id!!)
        val newAccessToken = generateAccessToken(email, memberRole.role.name)
        val newRefreshToken = generateRefreshToken(email)
        val tokenInfo = TokenInfo("Bearer", newAccessToken, newRefreshToken).also {
            logger.debug("토큰 재발급 정보")
            logger.debug("accessToken : {}", it.accessToken)
            logger.debug("refreshToken : {}", it.refreshToken)
        }

        // 새로운 Refresh Token 정보를 DB에 저장
        refreshTokenRepository.update(email, newRefreshToken)

        return MemberLoginResponseDto(member.email, member.name, tokenInfo)
    }

    private fun generateAccessToken(name: String, authorities: String): String {
        val now = Date()
        val expireDate = Date(now.time + ACCESS_EXPIRATION_MILLISECOND)
        logger.debug("Access Token Expires : {}", expireDate)
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
        val expireDate = Date(now.time + REFRESH_EXPIRATION_MILLISECOND)
        logger.debug("Refresh Token Expires : {}", expireDate)
        return Jwts.builder()
            .setSubject(name)
            .setIssuedAt(now)
            .setExpiration(expireDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token)

        val auth = claims["auth"] ?: throw RuntimeException("토큰에 인증 관련 정보가 없습니다.")

        // 권한 정보 추출
        val authorities: Collection<GrantedAuthority> = (auth as String)
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String): JwtCode {
        return try {
            getClaims(token)
            JwtCode.ACCESS
        } catch (e: Exception) {
            logger.error(e.message)
            when(e) {
                is SecurityException -> JwtCode.SECURITY_ERROR
                is MalformedJwtException -> JwtCode.MALFORMED
                is ExpiredJwtException -> JwtCode.EXPIRED
                is UnsupportedJwtException -> JwtCode.UNSUPPORTED
                is IllegalArgumentException -> JwtCode.ILLEGAL_ARGUMENT
                else -> JwtCode.UNKNOWN
            }
        }
    }

    private fun getClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

    companion object {
        fun generateRefreshTokenCookie(refreshTokenValue: String): ResponseCookie = ResponseCookie.from("refreshToken")
            .value(refreshTokenValue)
            .path("/")
            .maxAge(REFRESH_EXPIRATION_MILLISECOND)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .build()

        fun generateAccessTokenCookie(accessTokenValue: String): ResponseCookie = ResponseCookie.from("x-access-token")
            .value(accessTokenValue)
            .maxAge(ACCESS_EXPIRATION_MILLISECOND)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .build()
    }
}
