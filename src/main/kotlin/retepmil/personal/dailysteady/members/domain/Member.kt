package retepmil.personal.dailysteady.members.domain

import jakarta.persistence.*
import retepmil.personal.dailysteady.common.domain.BaseTime
import retepmil.personal.dailysteady.common.security.domain.MemberRole

@Entity
data class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 50, unique = true)
    var email: String,

    @Column(nullable = false, length = 100)
    val password: String,

    @Column(nullable = false, length = 50)
    var name: String,
) : BaseTime() {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    val memberRole: List<MemberRole>? = null
}