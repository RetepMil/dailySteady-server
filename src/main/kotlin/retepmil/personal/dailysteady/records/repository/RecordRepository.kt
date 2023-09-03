package retepmil.personal.dailysteady.records.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import retepmil.personal.dailysteady.records.domain.Record
import java.time.LocalDate

@Repository
interface RecordRepository : JpaRepository<Record, Long> {
    @Query(value = "SELECT r.* FROM Record r WHERE DATE(created_at) =:date AND member_email =:email", nativeQuery = true)
    fun getRecordsByDate(email: String, date: LocalDate): List<Record>
}