package ru.andreikud.mvvmtodo.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import ru.andreikud.mvvmtodo.data.model.Task
import ru.andreikud.mvvmtodo.util.Constants
import javax.inject.Inject

sealed class AddEditTaskEvent {
    data class InvalidName(val msg: String) : AddEditTaskEvent()
    data class Updated(val result: Int) : AddEditTaskEvent()
    data class Created(val result: Int) : AddEditTaskEvent()
}

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val task = savedStateHandle.get<Task>("task")

    var taskName = savedStateHandle.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            savedStateHandle.set("taskName", value)
        }

    var isTaskImportant =
        savedStateHandle.get<Boolean>("isImportant") ?: task?.isImportant ?: false
        set(value) {
            field = value
            savedStateHandle.set("isImportant", value)
        }

    private val eventsChannel = Channel<AddEditTaskEvent>()
    val events = eventsChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank() || taskName.any { c -> c.isDigit() }) {
            sendEvent(AddEditTaskEvent.InvalidName(taskName))
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, isImportant = isTaskImportant)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, isImportant = isTaskImportant)
            createTask(newTask)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        sendEvent(AddEditTaskEvent.Created(Constants.ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        sendEvent(AddEditTaskEvent.Updated(Constants.EDIT_TASK_RESULT_OK))
    }

    private fun sendEvent(event: AddEditTaskEvent) = viewModelScope.launch {
        eventsChannel.send(event)
    }
}
