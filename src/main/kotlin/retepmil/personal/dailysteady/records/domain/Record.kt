package retepmil.personal.dailysteady.records.domain

import jakarta.persistence.*
import retepmil.personal.dailysteady.members.domain.Member
import java.time.LocalDateTime

@Entity
data class Record(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val recordId: Long?,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(referencedColumnName = "email")
    private val member: Member,

    @Column(nullable = false)
    private val createdAt: LocalDateTime,

    @Column(columnDefinition = "TEXT")
    private val content: String,
)