package retepmil.personal.dailysteady.members.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.security.crypto.password.PasswordEncoder

data class MemberLoginRequestDto(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @JsonProperty("password")
    private val _password: String?,
) {
    val password: String
        get() = _password!!
}