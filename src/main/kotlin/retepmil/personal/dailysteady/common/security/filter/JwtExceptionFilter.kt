package retepmil.personal.dailysteady.common.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import retepmil.personal.dailysteady.common.security.exception.InvalidTokenException

class JwtExceptionFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) = runCatching { filterChain.doFilter(request, response) }
        .getOrElse {
            setResponse(response, it)
            Unit
        }

    private fun setResponse(response: HttpServletResponse, ex: Throwable) =
        response.apply {
            contentType = "application/json"
            characterEncoding = "utf-8"
            status = if (ex is InvalidTokenException) HttpStatus.BAD_REQUEST.value()
                else HttpStatus.INTERNAL_SERVER_ERROR.value()
            writer.print(ex.message)
        }
}