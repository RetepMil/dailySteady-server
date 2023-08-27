package retepmil.personal.dailysteady.records.controller

import com.fasterxml.jackson.annotation.JsonFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import retepmil.personal.dailysteady.common.dto.BaseResponseDto
import retepmil.personal.dailysteady.common.dto.DataResponseDto
import retepmil.personal.dailysteady.records.dto.RecordGetRequestDto
import retepmil.personal.dailysteady.records.dto.RecordSaveRequestDto
import retepmil.personal.dailysteady.records.service.RecordService
import java.time.LocalDate

@RestController
@RequestMapping
class RecordController(
    private val recordService: RecordService,
) {
    private val logger: Logger = LoggerFactory.getLogger(RecordController::class.java)

    @PostMapping("/record")
    fun saveRecord(@RequestBody request: RecordSaveRequestDto): BaseResponseDto {
        logger.debug("POST /record")
        val newRecord = recordService.saveRecord(request)
        return DataResponseDto(201, newRecord)
    }

    @GetMapping("/record")
    fun getRecords(
        @RequestParam("member_email")
        userId: String,

        @RequestParam("date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        date: LocalDate,
    ): BaseResponseDto = DataResponseDto(200, recordService.getLogs(RecordGetRequestDto(userId, date)))
}