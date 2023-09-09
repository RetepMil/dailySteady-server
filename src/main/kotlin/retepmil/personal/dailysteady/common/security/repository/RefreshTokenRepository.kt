package retepmil.personal.dailysteady.common.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import retepmil.personal.dailysteady.common.security.domain.RefreshToken


@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByEmail(tokenValue: String): RefreshToken?
}