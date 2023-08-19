package retepmil.personal.dailysteady.members.dto

import retepmil.personal.dailysteady.members.domain.Member

data class MemberCreateRequestDto(
    val email: String,
    val username: String,
    val password: String,
) {
    fun toEntity() = Member(null, email, username, password)
}