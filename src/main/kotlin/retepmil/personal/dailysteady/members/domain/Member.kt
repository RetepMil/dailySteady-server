package retepmil.personal.dailysteady.members.domain

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import retepmil.personal.dailysteady.common.domain.BaseTime
import retepmil.personal.dailysteady.common.security.domain.MemberRole

@Entity
data class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 50, unique = true)
    var email: String,

    @Column(nullable = false, length = 100)
    private val password: String,

    @Column(nullable = false, length = 50)
    var name: String,
) : BaseTime(), UserDetails {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    var memberRole: List<MemberRole>? = null

    override fun getAuthorities() =
        memberRole?.map { SimpleGrantedAuthority(it.role.name) }?.toMutableList() ?: mutableListOf<GrantedAuthority>()

    override fun getPassword(): String = this.password

    override fun getUsername(): String = this.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}