package retepmil.personal.dailysteady.members.controller

import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import retepmil.personal.dailysteady.common.dto.BaseResponse
import retepmil.personal.dailysteady.common.security.TokenInfo
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginRequestDto
import retepmil.personal.dailysteady.members.service.MemberService

@RestController
@RequestMapping("member")
class MemberController(
    private val memberService: MemberService,
) {

    private val log: Logger = LoggerFactory.getLogger(MemberController::class.java)

    @PostMapping("/signup")
    fun registerNewMember(@RequestBody @Valid request: MemberCreateRequestDto): BaseResponse<String> {
        log.debug("MemberController -> registerNewMember 함수 진입")
        memberService.saveMember(request)
        return BaseResponse("회원가입을 성공했습니다")
    }

    @PostMapping("/signin")
    fun signin(@RequestBody @Valid request: MemberLoginRequestDto): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.signin(request)
        return BaseResponse(data = tokenInfo)
    }

}