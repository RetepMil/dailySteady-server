package retepmil.personal.dailysteady.members.service

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import retepmil.personal.dailysteady.common.security.domain.MemberRole
import retepmil.personal.dailysteady.common.security.domain.RefreshToken
import retepmil.personal.dailysteady.common.security.exception.InvalidRefreshTokenException
import retepmil.personal.dailysteady.common.security.exception.RefreshTokenNotFoundException
import retepmil.personal.dailysteady.common.security.jwt.JwtTokenProvider
import retepmil.personal.dailysteady.common.security.repository.MemberRoleRepository
import retepmil.personal.dailysteady.common.security.repository.RefreshTokenRepository
import retepmil.personal.dailysteady.common.security.status.ROLE
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
    private val refreshTokenRepository: RefreshTokenRepository,
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
        logger.debug("로그인 서비스 로직 실행")

        val authenticationToken = UsernamePasswordAuthenticationToken(request.email, request.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        val tokenInfo = jwtTokenProvider.createToken(authentication)

        val refreshTokenValue = tokenInfo.refreshToken
        val refreshToken = RefreshToken(null, request.email, refreshTokenValue)

        refreshTokenRepository.save(refreshToken)

        val member = memberRepository.findByEmail(request.email) ?: throw MemberNotFoundException()
        val username = member.username

        return MemberLoginResponseDto(request.email, username, tokenInfo)
    }

    fun renewAccessToken(refreshTokenValue: String, accessTokenValue: String): MemberLoginResponseDto {
        logger.debug("Refresh Token 검증 로직 수행")

        val authentication = jwtTokenProvider.getAuthentication(accessTokenValue)
        val refreshToken = refreshTokenRepository.findByEmail(authentication.name)
            ?: throw RefreshTokenNotFoundException()

        val dbRefreshToken = refreshToken.refreshTokenValue
        refreshTokenRepository.delete(refreshToken)

        if (refreshTokenValue != dbRefreshToken) throw InvalidRefreshTokenException()

        /* -------------------- */
        /* -------------------- */

        logger.debug("Access Token 재발급 로직 수행")

        val tokenInfo = jwtTokenProvider.createToken(authentication)
        val newRefreshTokenValue = tokenInfo.refreshToken
        val newRefreshToken = RefreshToken(null, authentication.name, newRefreshTokenValue)

        refreshTokenRepository.save(newRefreshToken)

        val member = memberRepository.findByEmail(authentication.name) ?: throw MemberNotFoundException()

        return MemberLoginResponseDto(member.email, member.name, tokenInfo)
    }

    fun getMemberInfo(email: String): MemberInfoVO {
        val member = memberRepository.findByEmail(email)
            ?: throw InvalidParameterException("존재하지 않는 멤버를 조회할 수 없습니다")
        return MemberInfoVO.from(member)
    }
}