package retepmil.personal.dailysteady.members.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.security.crypto.password.PasswordEncoder

data class MemberLoginRequestDto(
    @field:Email
    val email: String = "",

    @JsonProperty("password")
    private val _password: String?,
) {
    val password: String
        get() = _password!!

    fun isTokenSignin() = this.email == "" || this._password == null
}