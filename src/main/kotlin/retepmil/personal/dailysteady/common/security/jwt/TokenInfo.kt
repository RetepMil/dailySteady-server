package retepmil.personal.dailysteady.common.security.jwt

import com.fasterxml.jackson.annotation.JsonIgnore

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
    @JsonIgnore
    val refreshToken: String,
)