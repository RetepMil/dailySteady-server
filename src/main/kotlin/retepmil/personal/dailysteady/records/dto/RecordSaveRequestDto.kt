package retepmil.personal.dailysteady.records.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class RecordSaveRequestDto(
    val email: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    val time: LocalDateTime,
    val content: String,
)