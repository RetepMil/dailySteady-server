package retepmil.personal.dailysteady.common.security.controller

import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import retepmil.personal.dailysteady.common.dto.BaseResponseDto
import retepmil.personal.dailysteady.common.dto.DataResponseDto
import retepmil.personal.dailysteady.common.security.jwt.TokenInfo
import retepmil.personal.dailysteady.members.domain.Member
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginRequestDto
import retepmil.personal.dailysteady.members.dto.MemberLoginResponseDto
import retepmil.personal.dailysteady.members.service.MemberService

@RestController
class AuthController(
    private val memberService: MemberService,
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: MemberCreateRequestDto): BaseResponseDto {
        logger.debug("SecurityController -> signup 함수 진입 :: 파라미터 : {}", request)
        memberService.signUp(request)
        return BaseResponseDto.of(201, "멤버 생성이 성공적으로 수행되었습니다")
    }

    @PostMapping("/signin")
    fun signin(@RequestBody @Valid request: MemberLoginRequestDto): DataResponseDto<MemberLoginResponseDto> {
        logger.debug("SecurityController -> signin 함수 진입 :: 파라미터 : {}", request)
        val responsesDto = memberService.signin(request)
        return DataResponseDto(200, responsesDto)
    }
}