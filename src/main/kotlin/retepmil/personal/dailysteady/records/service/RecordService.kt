package retepmil.personal.dailysteady.records.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import retepmil.personal.dailysteady.members.repository.MemberRepository
import retepmil.personal.dailysteady.records.domain.Record
import retepmil.personal.dailysteady.records.dto.RecordGetRequestDto
import retepmil.personal.dailysteady.records.dto.RecordSaveRequestDto
import retepmil.personal.dailysteady.records.repository.RecordRepository
import retepmil.personal.dailysteady.records.vo.RecordsVO

@Service
@Transactional
class RecordService @Autowired constructor(
    private val recordRepository: RecordRepository,
    private val memberRepository: MemberRepository,
) {
    fun saveRecord(request: RecordSaveRequestDto) {
        val memberEmail = request.email
        val requester = memberRepository.findByEmail(memberEmail)
        val newRecord = Record(null, requester, request.time, request.content)
        recordRepository.save(newRecord)
    }

    fun getLogs(request: RecordGetRequestDto): List<RecordsVO> {
        val getRecords = recordRepository.getRecordsByDate(request.email, request.date)
        println(getRecords)
        return listOf()
    }

    fun deleteRecord(recordId: Long) {
        val record = recordRepository.getReferenceById(recordId)
        recordRepository.delete(record)
    }
}