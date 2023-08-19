package retepmil.personal.dailysteady.common.dto

data class BaseResponse<T>(
    val resultCode: String = "success",
    val data: T? = null,
    val message: String = "정상 처리 되었습니다."
)