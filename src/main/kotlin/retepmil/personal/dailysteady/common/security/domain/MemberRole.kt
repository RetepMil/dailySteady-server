package retepmil.personal.dailysteady.common.security.domain

import jakarta.persistence.*
import retepmil.personal.dailysteady.common.security.status.ROLE
import retepmil.personal.dailysteady.members.domain.Member

@Entity
class MemberRole(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val role: ROLE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_member_role_member_id"))
    val member: Member,
)