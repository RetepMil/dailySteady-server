package retepmil.personal.dailysteady.records.vo

import java.time.LocalDateTime

data class RecordsVO (
    val recordId: Long,
    val email: String?,
    val createdAt: LocalDateTime,
    val content: String,
)