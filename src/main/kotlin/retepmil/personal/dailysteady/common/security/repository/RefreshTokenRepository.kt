package retepmil.personal.dailysteady.common.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import retepmil.personal.dailysteady.common.security.domain.RefreshToken

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByEmail(email: String): RefreshToken?

    @Query("UPDATE RefreshToken rt set rt.refreshTokenValue=:newValue where rt.email=:email")
    fun update(@Param("email") email: String, @Param("newValue") newTokenValue: String)

}