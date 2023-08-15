package retepmil.personal.dailysteady.records.repository

import jakarta.persistence.*
import retepmil.personal.dailysteady.records.vo.RecordsVO
import java.time.LocalDateTime

@Entity
data class Record(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val recordId: Long?,

    @Column(nullable = false)
    private val userId: String,

    @Column
    private val createdAt: LocalDateTime,

    @Column(columnDefinition = "TEXT")
    private val content: String,
) {
    fun toVO() = RecordsVO(this.recordId!!, this.userId, this.createdAt, this.content)
}