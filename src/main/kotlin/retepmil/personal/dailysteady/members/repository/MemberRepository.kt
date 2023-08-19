package retepmil.personal.dailysteady.members.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import retepmil.personal.dailysteady.members.domain.Member

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(memberEmail: String): Member
}