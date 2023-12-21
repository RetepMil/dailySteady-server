package retepmil.personal.dailysteady.todos.domain

import jakarta.persistence.*
import retepmil.personal.dailysteady.common.domain.BaseTime
import retepmil.personal.dailysteady.members.domain.Member

@Entity
data class Todo(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false) @ManyToOne @JoinColumn
    var member: Member,

    @Column(nullable = false, length = 200)
    var content: String,

    @Column(nullable = false)
    var isDone: Boolean,

) : BaseTime()