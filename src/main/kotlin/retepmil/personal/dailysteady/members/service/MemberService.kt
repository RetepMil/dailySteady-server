package retepmil.personal.dailysteady.members.service

import org.springframework.stereotype.Service
import retepmil.personal.dailysteady.members.dto.MemberCreateRequestDto
import retepmil.personal.dailysteady.members.repository.MemberRepository

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun saveMember(request: MemberCreateRequestDto): Boolean {
        memberRepository.save(request.toEntity())
        return true
    }
}