package retepmil.personal.dailysteady.records.domain

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import retepmil.personal.dailysteady.members.domain.Member
import java.time.LocalDateTime

@Entity
data class Record(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val recordId: Long?,

//    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
//    @JoinColumn(referencedColumnName = "email")
//    private val member: Member,
    @Column(nullable = false)
    val memberEmail: String,

    @Column(nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd")
    val createdAt: LocalDateTime,

    @Column(columnDefinition = "TEXT")
    val content: String,
)