package retepmil.personal.dailysteady.members.vo

import retepmil.personal.dailysteady.members.domain.Member

data class MemberInfoVO(
    val id: String,
    val email: String,
    val name: String
) {
    companion object {
        fun from(member: Member): MemberInfoVO =
            MemberInfoVO(member.id.toString(), member.email, member.name)
    }
}
