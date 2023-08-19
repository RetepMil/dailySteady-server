package retepmil.personal.dailysteady.common.security.controller

import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import retepmil.personal.dailysteady.common.dto.BaseResponse
import retepmil.personal.dailysteady.common.security.TokenInfo
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginRequestDto
import retepmil.personal.dailysteady.members.service.MemberService

@RestController
class AuthController(
    private val memberService: MemberService,
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: MemberCreateRequestDto): BaseResponse<String> {
        logger.debug("SecurityController -> signup 함수 진입 :: 파라미터 : {}", request)
        memberService.saveMember(request)
        return BaseResponse(data = "회원가입을 성공했습니다")
    }

    @PostMapping("/signin")
    fun signin(@RequestBody @Valid request: MemberLoginRequestDto): BaseResponse<TokenInfo> {
        logger.debug("SecurityController -> signin 함수 진입 :: 파라미터 : {}", request)
        val tokenInfo = memberService.signin(request)
        return BaseResponse(data = tokenInfo)
    }
}