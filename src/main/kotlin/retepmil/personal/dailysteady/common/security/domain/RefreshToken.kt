package retepmil.personal.dailysteady.common.security.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,

    @Column(nullable = false, length = 50, unique = true)
    val email: String,

    @Column(nullable = false)
    val refreshTokenValue: String,

    @Column(nullable = false)
    var expiredAt: LocalDateTime? = LocalDateTime.now().plusHours(24)
)
