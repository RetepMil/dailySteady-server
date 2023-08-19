package retepmil.personal.dailysteady.members.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import retepmil.personal.dailysteady.common.security.JwtTokenProvider
import retepmil.personal.dailysteady.common.security.TokenInfo
import retepmil.personal.dailysteady.common.security.domain.MemberRole
import retepmil.personal.dailysteady.common.security.repository.MemberRoleRepository
import retepmil.personal.dailysteady.common.security.status.ROLE
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginRequestDto
import retepmil.personal.dailysteady.members.repository.MemberRepository

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun saveMember(request: MemberCreateRequestDto) {
        if (memberRepository.findByEmail(request.email) != null)
            throw Exception("이미 등록된 이메일 입니다")

        val newMember = request.toEntity()
        memberRepository.save(newMember)

        val memberRole = MemberRole(null, ROLE.MEMBER, newMember)
        memberRoleRepository.save(memberRole)
    }

    fun signin(request: MemberLoginRequestDto): TokenInfo {
        val authenticationToken =
            UsernamePasswordAuthenticationToken(request.email, request.password)
        val authentication =
            authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return jwtTokenProvider.createToken(authentication)
    }
}