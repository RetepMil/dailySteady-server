package retepmil.personal.dailysteady.todos.repository

import org.springframework.data.jpa.repository.JpaRepository
import retepmil.personal.dailysteady.todos.domain.Todo

interface TodoRepository : JpaRepository<Todo, Long>