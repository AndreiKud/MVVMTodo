package ru.andreikud.mvvmtodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.data.PreferencesManager
import ru.andreikud.mvvmtodo.data.SortOrder
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import ru.andreikud.mvvmtodo.data.model.Task
import javax.inject.Inject

sealed class TaskEvent {
    data class TaskDeleted(val task: Task) : TaskEvent()
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val queryNameFilter = MutableStateFlow("")
    val preferences = preferencesManager.preferencesFlow

    @ExperimentalCoroutinesApi
    private val eventsChannel = Channel<TaskEvent>()

    @ExperimentalCoroutinesApi
    val events = eventsChannel.receiveAsFlow()

    @ExperimentalCoroutinesApi
    private val filteredTasks = combine(
        queryNameFilter,
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

    fun onTaskSelected(item: Task) {

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

    @ExperimentalCoroutinesApi
    val tasks = filteredTasks.asLiveData()
}
