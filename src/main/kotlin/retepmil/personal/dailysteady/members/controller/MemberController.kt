package retepmil.personal.dailysteady.members.controller

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import retepmil.personal.dailysteady.common.dto.BaseResponse
import retepmil.personal.dailysteady.members.service.MemberService
import retepmil.personal.dailysteady.members.vo.MemberInfoVO

@RestController
@RequestMapping("member")
class MemberController(
    private val memberService: MemberService,
) {
    private val logger: Logger = LoggerFactory.getLogger(MemberController::class.java)

    @GetMapping
    fun info(@RequestParam("email") @NotBlank @Email email: String): BaseResponse<MemberInfoVO> {
        logger.debug("MemberController -> info 함수 진입 :: 파라미터 : {}", email)
        val memberInfo = memberService.getMemberInfo(email)
        return BaseResponse(data = memberInfo)
    }
}