package retepmil.personal.dailysteady.members.domain

import jakarta.persistence.*
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.userdetails.UserDetails
import retepmil.personal.dailysteady.common.domain.BaseTime
import retepmil.personal.dailysteady.records.domain.Record

@Entity
data class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var username: String,

    @Column(name = "password", nullable = false)
    var encryptedPwd: String,
) : BaseTime()
//, UserDetails {
//    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
//
//    override fun getPassword(): String = encryptedPwd
//
//    override fun getUsername(): String = email
//
//    override fun isAccountNonExpired(): Boolean = true
//
//    override fun isAccountNonLocked(): Boolean = true
//
//    override fun isCredentialsNonExpired(): Boolean = true
//
//    override fun isEnabled(): Boolean = true
//}