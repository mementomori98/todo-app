package mox.todo.app.repositories

import mox.todo.app.util.LiveData
import mox.todo.app.models.Todo

interface TodoRepository {

    fun liveData(listId: Int? = null): LiveData<List<Todo>>
    fun add(todo: Todo, position: Int = 0): Todo
    fun delete(id: Int): Boolean

}