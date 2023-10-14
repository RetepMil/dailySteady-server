package retepmil.personal.dailysteady.members.service

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import retepmil.personal.dailysteady.common.security.domain.MemberRole
import retepmil.personal.dailysteady.common.security.domain.RefreshToken
import retepmil.personal.dailysteady.common.security.exception.InvalidTokenException
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
        logger.debug("로그인 서비스 로직 시작")

        var authentication = SecurityContextHolder.getContext().authentication
//        if (request.isTokenSignin() && authentication.principal == "anonymousUser")
//            throw InvalidTokenException("")

        if (!request.isTokenSignin()) {
            val authenticationToken = UsernamePasswordAuthenticationToken(request.email, request.password)
            authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
            SecurityContextHolder.getContext().authentication = authentication
            logger.debug("아이디/패스워드 로그인 성공")
        } else logger.debug("토큰 로그인 성공")

        val email = authentication.name
        val member = memberRepository.findByEmail(email)
            ?: throw MemberNotFoundException()

        val tokenInfo = jwtTokenProvider.createToken(authentication)
        val refreshTokenValue = tokenInfo.refreshToken

        // Refresh Token이 없다면 신규 발급
        val refreshToken = refreshTokenRepository.findByEmail(email)
        if (refreshToken == null) {
            val newRefreshToken = RefreshToken(null, email, refreshTokenValue)
            refreshTokenRepository.save(newRefreshToken)
        }

        // 있다면 Refresh Token 정보 업데이트
        else refreshTokenRepository.update(email, refreshToken.refreshTokenValue)

        return MemberLoginResponseDto(member.email, member.name, tokenInfo)
    }

    fun renewAccessToken(refreshTokenValue: String, authentication: Authentication): MemberLoginResponseDto {
        logger.debug("Access Token 재발급 로직 수행")

        val tokenInfo = jwtTokenProvider.createToken(authentication)
        val newRefreshTokenValue = tokenInfo.refreshToken
        val newRefreshToken = RefreshToken(null, authentication.name, newRefreshTokenValue)

        refreshTokenRepository.update(authentication.name, newRefreshToken.refreshTokenValue)

        val member = memberRepository.findByEmail(authentication.name)
            ?: throw MemberNotFoundException()

        return MemberLoginResponseDto(member.email, member.name, tokenInfo)
    }

    fun getMemberInfo(email: String): MemberInfoVO {
        val member = memberRepository.findByEmail(email)
            ?: throw InvalidParameterException("존재하지 않는 멤버를 조회할 수 없습니다")
        return MemberInfoVO.from(member)
    }

    fun tokenSignin(accessTokenValue: String): MemberLoginResponseDto {
        logger.debug("Access Token 재발급 로직 수행")
        val authentication = jwtTokenProvider.getAuthentication(accessTokenValue)
        val tokenInfo = jwtTokenProvider.createToken(authentication)

        val newRefreshTokenValue = tokenInfo.refreshToken
        val newRefreshToken = RefreshToken(null, authentication.name, newRefreshTokenValue)

        refreshTokenRepository.update(authentication.name, newRefreshToken.refreshTokenValue)

        val member = memberRepository.findByEmail(authentication.name)
            ?: throw MemberNotFoundException()

        return MemberLoginResponseDto(member.email, member.name, tokenInfo)
    }
}