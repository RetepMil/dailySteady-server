package retepmil.personal.dailysteady.common.security.exception

class InvalidTokenException(
    override val message: String?
) : Exception("토큰 값이 유효하지 않습니다")