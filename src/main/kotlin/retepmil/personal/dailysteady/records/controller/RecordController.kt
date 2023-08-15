package retepmil.personal.dailysteady.records.controller

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import retepmil.personal.dailysteady.records.dto.RecordGetRequestDto
import retepmil.personal.dailysteady.records.dto.RecordSaveRequestDto
import retepmil.personal.dailysteady.records.service.RecordService
import retepmil.personal.dailysteady.records.vo.RecordsVO
import java.time.LocalDate

@RestController
@RequestMapping
class RecordController(
    private val recordService: RecordService,
) {

    @PostMapping("/record")
    fun saveRecord(@RequestBody request: RecordSaveRequestDto): String {
        recordService.saveRecord(request)
        return "OK"
    }

    @GetMapping("/record")
    fun getRecords(
        @RequestParam("userId")
        userId: String,

        @RequestParam("date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        date: LocalDate,
    ): List<RecordsVO> {
        return recordService.getLogs(RecordGetRequestDto(userId, date))
    }

    @DeleteMapping("record")
    fun deleteRecord(@RequestParam("recordId") recordId: Long): String {
        recordService.deleteRecord(recordId)
        return "OK"
    }

}