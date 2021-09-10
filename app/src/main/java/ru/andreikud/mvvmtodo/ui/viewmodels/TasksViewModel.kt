package ru.andreikud.mvvmtodo.ui.viewmodels

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.data.PreferencesManager
import ru.andreikud.mvvmtodo.data.SortOrder
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import ru.andreikud.mvvmtodo.data.model.Task
import ru.andreikud.mvvmtodo.util.Constants
import javax.inject.Inject

sealed class TaskEvent {
    object NavigateToAddTaskScreen : TaskEvent()
    data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
    data class TaskDeleted(val task: Task) : TaskEvent()
    data class ShowConfirmationMethod(val msg: String) : TaskEvent()
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val queryNameFilter = savedStateHandle.getLiveData("searchQeury", "")
    val preferences = preferencesManager.preferencesFlow

    @ExperimentalCoroutinesApi
    private val eventsChannel = Channel<TaskEvent>()

    @ExperimentalCoroutinesApi
    val events = eventsChannel.receiveAsFlow()

    @ExperimentalCoroutinesApi
    private val filteredTasks = combine(
        queryNameFilter.asFlow(),
        preferences,
    ) { query, preferences ->
        Pair(query, preferences)
    }.flatMapLatest { (query, preferences) ->
        dao.query(query, preferences.sortOrder, preferences.hideCompleted)
    }

    fun onSortOrderClicked(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClicked(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        eventsChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onCompletedStateChanged(item: Task, checked: Boolean) = viewModelScope.launch {
        dao.update(item.copy(isCompleted = checked))
    }

    @ExperimentalCoroutinesApi
    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        dao.delete(task)
        eventsChannel.send(TaskEvent.TaskDeleted(task))
    }

    fun undoDeletion(task: Task) = viewModelScope.launch {
        dao.insert(task)
    }

    fun onTaskAddClick() = viewModelScope.launch {
        eventsChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            Constants.EDIT_TASK_RESULT_OK -> {
                showTaskSavedConfirmationMessage("Task edited")
            }
            Constants.ADD_TASK_RESULT_OK -> {
                showTaskSavedConfirmationMessage("Task added")
            }
        }
    }

    private fun showTaskSavedConfirmationMessage(msg: String) = viewModelScope.launch {
        eventsChannel.send(TaskEvent.ShowConfirmationMethod(msg))
    }

    @ExperimentalCoroutinesApi
    val tasks = filteredTasks.asLiveData()
}
