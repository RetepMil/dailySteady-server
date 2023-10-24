package retepmil.personal.dailysteady.common.security.controller

import jakarta.servlet.http.HttpServletRequest
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
    private val jwtTokenProvider: JwtTokenProvider,
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid requestDto: MemberCreateRequestDto): BaseResponseDto {
        logger.debug("SecurityController -> signup :: 파라미터 : {}", requestDto)
        memberService.signUp(requestDto)
        return BaseResponseDto.of(201, "멤버 생성이 성공적으로 수행되었습니다")
    }

    @PostMapping("/signin")
    fun signin(
        @RequestBody @Valid requestDto: MemberLoginRequestDto,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): DataResponseDto<MemberLoginResponseDto> {
        logger.debug("SecurityController -> signin 함수 진입 :: 파라미터 : {}", requestDto)

        val responseDto = memberService.signin(requestDto)

        // 쿠키에 Access Token 주입
        val accessTokenCookie = JwtTokenProvider.generateAccessTokenCookie(responseDto.tokenInfo.accessToken)
        response.addHeader("Set-Cookie", accessTokenCookie.toString())

        // 쿠키에 Refresh Token 주입
        val refreshTokenCookie = JwtTokenProvider.generateRefreshTokenCookie(responseDto.tokenInfo.refreshToken)
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())

        return DataResponseDto(200, responseDto)
    }

    @PatchMapping("/token")
    fun renewToken(
        @CookieValue("refreshToken") refreshToken: String,
        @CookieValue("x-access-token") accessToken: String,
        response: HttpServletResponse,
    ): DataResponseDto<MemberLoginResponseDto> {
        logger.debug("SecurityController -> renewToken 함수 진입")

        logger.debug("{} ||", refreshToken)
        logger.debug("{} ||", accessToken)

        val responseDto = jwtTokenProvider.renewToken(accessToken, refreshToken)

        // 쿠키에 Access Token 주입
        val accessTokenCookie = JwtTokenProvider.generateAccessTokenCookie(responseDto.tokenInfo.accessToken)
        response.addHeader("Set-Cookie", accessTokenCookie.toString())

        // 쿠키에 Refresh Token 주입
        val refreshTokenCookie = JwtTokenProvider.generateRefreshTokenCookie(responseDto.tokenInfo.refreshToken)
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())

        return DataResponseDto(200, responseDto)
    }

}