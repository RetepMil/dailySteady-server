package retepmil.personal.dailysteady.records.dto

import com.fasterxml.jackson.annotation.JsonFormat
import retepmil.personal.dailysteady.records.repository.Record
import java.time.LocalDateTime

data class RecordSaveRequestDto(
    private val userId: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private val time: LocalDateTime,
    private val content: String,
) {
    fun toEntity() = Record(null, userId, time, content)
}