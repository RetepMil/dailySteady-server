package retepmil.personal.dailysteady.common.exception

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import retepmil.personal.dailysteady.common.dto.BaseResponseDto
import retepmil.personal.dailysteady.common.dto.DataResponseDto
import retepmil.personal.dailysteady.common.security.exception.InvalidTokenException
import retepmil.personal.dailysteady.common.security.exception.RefreshTokenNotFoundException
import retepmil.personal.dailysteady.members.exception.MemberDuplicateException
import retepmil.personal.dailysteady.members.exception.MemberNotFoundException

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleValidationExceptions(ex: MethodArgumentNotValidException): BaseResponseDto {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { err ->
            val fieldName = (err as FieldError).field
            val errorMessage = err.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }
        return DataResponseDto(404, errors)
    }

    @ExceptionHandler(BadCredentialsException::class)
    protected fun handleValidationExceptions(ex: BadCredentialsException): BaseResponseDto {
        ex.printStackTrace()
        val errors = mapOf("로그인 실패" to "이메일 혹은 비밀번호를 다시 확인하세요")
        return DataResponseDto(404, errors)
    }

    @ExceptionHandler(AccessTokenExpiredException::class)
    protected fun handleMemberDuplicateException(ex: AccessTokenExpiredException): BaseResponseDto =
        BaseResponseDto.of(404, ex.message!!)

    @ExceptionHandler(MemberDuplicateException::class)
    protected fun handleMemberDuplicateException(ex: MemberDuplicateException): BaseResponseDto =
        BaseResponseDto.of(404, ex.message!!)

    @ExceptionHandler(MemberNotFoundException::class)
    protected fun handleMemberNotFoundException(ex: MemberNotFoundException): BaseResponseDto =
        BaseResponseDto.of(404, ex.message!!)

    @ExceptionHandler(RefreshTokenNotFoundException::class)
    protected fun handleRefreshTokenNotFoundException(ex: RefreshTokenNotFoundException): BaseResponseDto =
        BaseResponseDto.of(404, ex.message!!)

    @ExceptionHandler(InvalidTokenException::class)
    protected fun handleInvalidRefreshTokenException(ex: InvalidTokenException): BaseResponseDto =
        BaseResponseDto.of(404, ex.message!!)
}