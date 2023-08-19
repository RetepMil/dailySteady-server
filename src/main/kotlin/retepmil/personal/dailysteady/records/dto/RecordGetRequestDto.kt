package retepmil.personal.dailysteady.records.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class RecordGetRequestDto(
    val email: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate,
)