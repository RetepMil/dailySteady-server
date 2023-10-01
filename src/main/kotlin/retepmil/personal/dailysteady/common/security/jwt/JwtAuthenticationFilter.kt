package retepmil.personal.dailysteady.common.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import retepmil.personal.dailysteady.common.security.exception.InvalidTokenException

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain?) {
        logger.debug("JWT 필터 로직 실행")
        val httpRequest = request as HttpServletRequest

        if (httpRequest.requestURI == "/health") {
            chain?.doFilter(request, response)
            return
        }

        // HttpServletRequest의 accessToken 값에 대한 검증을 실시
        val token = resolveAccessToken(httpRequest)
        if (token != null && jwtTokenProvider.validateToken(token) == JwtCode.ACCESS) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        } else if (token != null && jwtTokenProvider.validateToken(token) == JwtCode.EXPIRED) {
            logger.debug("Access Token 유효기간 만료")
            val email = request.getHeader("Authorization")
            val givenRefreshTokenValue = request.cookies.filter { it.name == "refreshToken" }[0].value
            val storedRefreshToken = jwtTokenProvider.getRefreshToken(email)
            if (givenRefreshTokenValue != storedRefreshToken.refreshTokenValue)
                throw InvalidTokenException()
            if (jwtTokenProvider.validateToken(givenRefreshTokenValue) == JwtCode.ACCESS) {
                logger.debug("Refresh Token 유효, Access Token 재발급")
                val authentication = jwtTokenProvider.getAuthentication(token)
                val newToken = jwtTokenProvider.createToken(authentication)
                SecurityContextHolder.getContext().authentication = authentication
                (response as HttpServletResponse).setHeader(HttpHeaders.AUTHORIZATION, newToken.accessToken)
            }
        }
        chain?.doFilter(request, response)
    }

    private fun resolveAccessToken(request: HttpServletRequest): String? {
        logger.debug("JWT accessToken 검증 시작")
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            bearerToken.substring(7)
        } else request.cookies?.find { it.name == "x-access-token" }?.value
    }
}