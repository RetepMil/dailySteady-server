package retepmil.personal.dailysteady.members.service

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import retepmil.personal.dailysteady.common.security.jwt.JwtTokenProvider
import retepmil.personal.dailysteady.common.security.jwt.TokenInfo
import retepmil.personal.dailysteady.common.security.domain.MemberRole
import retepmil.personal.dailysteady.common.security.repository.MemberRoleRepository
import retepmil.personal.dailysteady.common.security.status.ROLE
import retepmil.personal.dailysteady.members.domain.Member
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginResponseDto
import retepmil.personal.dailysteady.members.exception.MemberDuplicateException
import retepmil.personal.dailysteady.members.exception.MemberNotFoundException
import retepmil.personal.dailysteady.members.repository.MemberRepository
import retepmil.personal.dailysteady.members.vo.MemberInfoVO
import java.security.InvalidParameterException

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {
    private val logger = LoggerFactory.getLogger(MemberService::class.java)

    fun signUp(request: MemberCreateRequestDto) {
        if (memberRepository.findByEmail(request.email) != null)
            throw MemberDuplicateException()

        val newMember = request.toEntity(passwordEncoder)
        memberRepository.save(newMember)

        val memberRole = MemberRole(null, ROLE.MEMBER, newMember)
        memberRoleRepository.save(memberRole)
    }

    fun signin(request: MemberLoginRequestDto): MemberLoginResponseDto {
        logger.debug("{}, {}", request.email, passwordEncoder.encode(request.password))

        val authenticationToken =
            UsernamePasswordAuthenticationToken(request.email, request.password)
        logger.debug("{}", authenticationToken.toString())

        val authentication =
            authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        logger.debug("{}", authentication.toString())

        val tokenInfo = jwtTokenProvider.createToken(authentication = authenticationToken)
        val member = memberRepository.findByEmail(request.email) ?:
            throw MemberNotFoundException()
        val username = member.username

        return MemberLoginResponseDto(request.email, username, tokenInfo)
    }

    fun getMemberInfo(email: String): MemberInfoVO {
        val member = memberRepository.findByEmail(email)
            ?: throw InvalidParameterException("존재하지 않는 멤버를 조회할 수 없습니다")
        return MemberInfoVO.from(member)
    }
}