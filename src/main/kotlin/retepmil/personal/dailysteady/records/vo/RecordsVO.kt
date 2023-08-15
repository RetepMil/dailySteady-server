package retepmil.personal.dailysteady.records.vo

import java.time.LocalDateTime

data class RecordsVO (
    val recordId: Long,
    val userId: String,
    val createdAt: LocalDateTime,
    val content: String,
)