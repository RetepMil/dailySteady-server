package retepmil.personal.dailysteady.members.dto

import retepmil.personal.dailysteady.common.security.jwt.TokenInfo

data class MemberLoginResponseDto(
    val email: String,
    val name: String,
    val tokenInfo: TokenInfo
)