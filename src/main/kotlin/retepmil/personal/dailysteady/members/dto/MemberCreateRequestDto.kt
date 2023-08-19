package retepmil.personal.dailysteady.members.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import retepmil.personal.dailysteady.members.domain.Member

data class MemberCreateRequestDto(
    val id: Long?,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Pattern(
        regexp="^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요"
    )
    val password: String,

    @field:NotBlank
    val username: String,
) {
    fun toEntity() = Member(null, email, password, username)
}