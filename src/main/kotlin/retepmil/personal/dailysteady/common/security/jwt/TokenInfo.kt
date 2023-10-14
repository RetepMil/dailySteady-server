package retepmil.personal.dailysteady.common.security.jwt

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
)