package retepmil.personal.dailysteady.common.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import retepmil.personal.dailysteady.common.security.domain.MemberRole

@Repository
interface MemberRoleRepository : JpaRepository<MemberRole, Long>