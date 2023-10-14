package retepmil.personal.dailysteady.common.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import retepmil.personal.dailysteady.common.security.exception.InvalidTokenException
import retepmil.personal.dailysteady.common.security.jwt.JwtCode
import retepmil.personal.dailysteady.common.security.jwt.JwtTokenProvider

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest

        if (httpRequest.requestURI == "/health") {
            chain?.doFilter(request, response)
            return
        }

        logger.debug("JWT 필터 로직 실행")

        val token = resolveAccessToken(httpRequest)
        if (token != null) {
            when (jwtTokenProvider.validateToken(token)) {
                JwtCode.ACCESS -> {
                    val authentication = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                }
                JwtCode.EXPIRED -> throw InvalidTokenException("토큰이 만료되었습니다")
                JwtCode.SECURITY_ERROR -> throw InvalidTokenException("토큰에 보안 관련 문제가 있습니다")
                JwtCode.MALFORMED -> throw InvalidTokenException("토큰에 문제가 있습니다")
                JwtCode.UNSUPPORTED -> throw InvalidTokenException("시스템에서 허용하는 토큰 스펙이 아닙니다")
                JwtCode.ILLEGAL_ARGUMENT -> throw InvalidTokenException("토큰 내용이 없습니다")
                else -> throw ServletException("예기치 못한 문제가 발생했습니다")
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