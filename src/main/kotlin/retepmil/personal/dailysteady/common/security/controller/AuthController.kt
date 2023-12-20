package retepmil.personal.dailysteady.common.security.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import retepmil.personal.dailysteady.common.dto.BaseResponseDto
import retepmil.personal.dailysteady.common.dto.DataResponseDto
import retepmil.personal.dailysteady.common.security.jwt.JwtTokenProvider
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginRequestDto
import retepmil.personal.dailysteady.members.service.MemberService

@RestController
class AuthController(
    private val memberService: MemberService,
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
    ): DataResponseDto<*> {
        logger.debug("SecurityController -> signin 함수 진입 :: 파라미터 : {}", requestDto)

        val authInfo = request.getAttribute("authentication") as Authentication?
        if (authInfo != null) {
            logger.debug("Already Logged In :: {}", authInfo)
            return DataResponseDto(200, "이미 인증된 사용자")
        }

        val responseDto = memberService.signin(requestDto)

        // 쿠키에 Access Token 주입
        val accessTokenCookie = JwtTokenProvider.generateAccessTokenCookie(responseDto.tokenInfo.accessToken)
        response.addHeader("Set-Cookie", accessTokenCookie.toString())

        // 쿠키에 Refresh Token 주입
        val refreshTokenCookie = JwtTokenProvider.generateRefreshTokenCookie(responseDto.tokenInfo.refreshToken)
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())

        return DataResponseDto(200, responseDto)
    }

}