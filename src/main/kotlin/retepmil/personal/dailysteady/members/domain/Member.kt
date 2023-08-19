package retepmil.personal.dailysteady.members.domain

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.userdetails.UserDetails
import retepmil.personal.dailysteady.common.domain.BaseTime
import retepmil.personal.dailysteady.common.security.domain.MemberRole
import retepmil.personal.dailysteady.records.domain.Record

@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(name = "uk_member_email", columnNames = ["email"])
])
data class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 50)
    var email: String,

    @Column(nullable = false, length = 20)
    val password: String,

    @Column(nullable = false, length = 50)
    var name: String,
) : BaseTime() {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    val memberRole: List<MemberRole>? = null
}