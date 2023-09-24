package retepmil.personal.dailysteady.common.security.domain

import jakarta.persistence.*

@Entity
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,

    @Column(nullable = false, length = 50, unique = true)
    val email: String,

    @Column(nullable = false)
    val refreshTokenValue: String,

    @Column(nullable = false)
    val expiresAt: String,
)
