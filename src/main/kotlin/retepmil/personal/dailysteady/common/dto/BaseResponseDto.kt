package retepmil.personal.dailysteady.common.dto

import org.springframework.http.HttpStatus

@Suppress("unused")
open class BaseResponseDto (
    var success: Boolean,
    open var code: Int,
    open var message: String,
) {

    companion object {
        fun of(code: Int, message: String): BaseResponseDto {
            val httpStatus = HttpStatus.valueOf(code)
            return when (httpStatus.value()) {
                in 200..299 -> BaseResponseDto(true, httpStatus.value(), message)
                in 400..599 -> BaseResponseDto(false, httpStatus.value(), message)
                else -> BaseResponseDto(false, 500, "서버 동작 오류입니다")
            }
        }

        fun ofCode(code: Int): BaseResponseDto {
            val httpStatus = HttpStatus.valueOf(code)
            return when (httpStatus.value()) {
                in 200..299 -> this.of(httpStatus.value(), httpStatus.reasonPhrase)
                in 400..599 -> this.of(httpStatus.value(), httpStatus.reasonPhrase)
                else -> this.of(500, "서버 동작 오류입니다")
            }
        }
    }
}

data class DataResponseDto<T>(
    override var code: Int,
    override var message: String,
    val data: T,
) : BaseResponseDto(code in 200..299, code, message) {
    constructor(code: Int, data: T) : this(code, "", data)
}