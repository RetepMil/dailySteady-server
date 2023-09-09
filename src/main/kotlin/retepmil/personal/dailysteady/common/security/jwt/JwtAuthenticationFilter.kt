package retepmil.personal.dailysteady.common.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain?) {
        logger.debug("JWT 필터 로직 실행")

        // HttpServletRequest의 accessToken 값에 대한 검증을 실시
        val token = resolveAccessToken(request as HttpServletRequest)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain?.doFilter(request, response)
    }

    private fun resolveAccessToken(request: HttpServletRequest): String? {
        logger.debug("JWT accessToken 검증 시작")

        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}