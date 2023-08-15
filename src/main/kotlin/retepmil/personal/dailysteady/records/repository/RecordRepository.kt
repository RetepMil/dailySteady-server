package retepmil.personal.dailysteady.records.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface RecordRepository : JpaRepository<Record, Long> {
    @Query(value = "SELECT r.* FROM Record r WHERE DATE(createdat) =:date AND userid =:userId", nativeQuery = true)
    fun getRecordsByUserIdAndDate(userId: String, date: LocalDate): List<Record>
}