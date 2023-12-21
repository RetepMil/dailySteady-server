package retepmil.personal.dailysteady.records.domain

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Record(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val recordId: Long?,

    @Column(nullable = false)
    val memberEmail: String,

    @Column(nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,

    @Column(columnDefinition = "TEXT")
    val content: String,

)