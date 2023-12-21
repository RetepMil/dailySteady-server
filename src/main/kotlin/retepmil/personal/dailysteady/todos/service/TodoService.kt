package retepmil.personal.dailysteady.todos.service

import org.springframework.stereotype.Service
import retepmil.personal.dailysteady.todos.domain.Todo
import retepmil.personal.dailysteady.todos.exception.TodoNotFoundException
import retepmil.personal.dailysteady.todos.repository.TodoRepository
import kotlin.jvm.optionals.getOrNull

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) {

    fun addTodo(todo: Todo): Todo = todoRepository.save(todo)

    fun deleteTodo(todoId: Long) = todoRepository.deleteById(todoId)

    fun markAsDone(todoId: Long): Todo {
        val todo = todoRepository.findById(todoId).getOrNull()
            ?: throw TodoNotFoundException()

        return todoRepository.save(todo.apply { this.isDone = true })
    }

}