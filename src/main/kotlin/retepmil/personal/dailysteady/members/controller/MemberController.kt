package retepmil.personal.dailysteady.members.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.service.MemberService

@RestController
@RequestMapping("member")
class MemberController(
    private val memberService: MemberService,
) {

    private val log: Logger = LoggerFactory.getLogger(MemberController::class.java)

    @PostMapping
    fun registerNewMember(@RequestBody request: MemberCreateRequestDto): String {
        log.debug("MemberController -> registerNewMember 함수 진입")
        memberService.saveMember(request)
        return "OK"
    }

}