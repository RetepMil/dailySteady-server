package retepmil.personal.dailysteady.records.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import retepmil.personal.dailysteady.members.repository.MemberRepository
import retepmil.personal.dailysteady.records.domain.Record
import retepmil.personal.dailysteady.records.dto.RecordGetRequestDto
import retepmil.personal.dailysteady.records.dto.RecordSaveRequestDto
import retepmil.personal.dailysteady.records.repository.RecordRepository

@Service
@Transactional
class RecordService (
    private val recordRepository: RecordRepository,
    private val memberRepository: MemberRepository,
) {
    fun saveRecord(request: RecordSaveRequestDto): Record {
        val memberEmail = request.email
        val requester = memberRepository.findByEmail(memberEmail) ?: throw Exception("유저가 존재하지 않습니다")

        val newRecord = Record(null, requester.email, request.time, request.content)
        recordRepository.save(newRecord)

        return newRecord
    }

    fun getLogs(request: RecordGetRequestDto): List<Record> {

        return recordRepository.getRecordsByDate(request.email, request.date)
    }

    fun deleteRecord(recordId: Long) {
        val record = recordRepository.getReferenceById(recordId)
        recordRepository.delete(record)
    }
}