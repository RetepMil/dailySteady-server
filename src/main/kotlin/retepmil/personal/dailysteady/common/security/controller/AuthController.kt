package retepmil.personal.dailysteady.common.security.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import retepmil.personal.dailysteady.common.dto.BaseResponseDto
import retepmil.personal.dailysteady.common.dto.DataResponseDto
import retepmil.personal.dailysteady.common.security.jwt.JwtTokenProvider
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginResponseDto
import retepmil.personal.dailysteady.members.service.MemberService
import java.util.*

@RestController
class AuthController(
    private val memberService: MemberService,
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid requestDto: MemberCreateRequestDto): BaseResponseDto {
        logger.debug("SecurityController -> signup 함수 진입 :: 파라미터 : {}", requestDto)
        memberService.signUp(requestDto)
        return BaseResponseDto.of(201, "멤버 생성이 성공적으로 수행되었습니다")
    }

    @PostMapping("/signin")
    fun signin(
        @RequestBody @Valid
        requestDto: MemberLoginRequestDto,
        response: HttpServletResponse,
    ): DataResponseDto<MemberLoginResponseDto> {
        logger.debug("SecurityController -> signin 함수 진입 :: 파라미터 : {}", requestDto)

        val responseDto = memberService.signin(requestDto)

        // 쿠키에 RefreshToken 주입
        val refreshTokenCookie = JwtTokenProvider.generateRefreshTokenCookie(responseDto.tokenInfo.refreshToken)
        response.setHeader("Set-Cookie", refreshTokenCookie.toString())

        return DataResponseDto(200, responseDto)
    }

    @PatchMapping("/member/authentication")
    fun renewAccessToken(
        @CookieValue("x-access-token") accessTokenValue: String,
        @CookieValue("refreshToken") refreshTokenValue: String,
        response: HttpServletResponse,
    ): DataResponseDto<MemberLoginResponseDto> {
        logger.debug("SecurityController -> renewAccessToken 함수 진입")

        val responseDto = memberService.renewAccessToken(accessTokenValue, refreshTokenValue)

        // 쿠키에 RefreshToken 주입
        val refreshTokenCookie = JwtTokenProvider.generateRefreshTokenCookie(responseDto.tokenInfo.refreshToken)
        response.setHeader("Set-Cookie", refreshTokenCookie.toString())

        return DataResponseDto(200, responseDto)
    }

}