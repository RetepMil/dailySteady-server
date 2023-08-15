package retepmil.personal.dailysteady.records.service

import org.springframework.stereotype.Service
import retepmil.personal.dailysteady.records.dto.RecordGetRequestDto
import retepmil.personal.dailysteady.records.dto.RecordSaveRequestDto
import retepmil.personal.dailysteady.records.repository.Record
import retepmil.personal.dailysteady.records.repository.RecordRepository
import retepmil.personal.dailysteady.records.vo.RecordsVO

@Service
class RecordService(
    private val recordRepository: RecordRepository
) {
    fun saveRecord(request: RecordSaveRequestDto) {
        val record = request.toEntity()
        recordRepository.save(record)
    }

    fun getLogs(request: RecordGetRequestDto): List<RecordsVO> {
        val getRecords =recordRepository.getRecordsByUserIdAndDate(request.userId, request.date)
        return getRecords.map { it.toVO() }
    }
}