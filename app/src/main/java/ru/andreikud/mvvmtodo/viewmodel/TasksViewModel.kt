package ru.andreikud.mvvmtodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.data.PreferencesManager
import ru.andreikud.mvvmtodo.data.SortOrder
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val queryNameFilter = MutableStateFlow("")
    val preferences = preferencesManager.preferencesFlow

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

    @ExperimentalCoroutinesApi
    val tasks = filteredTasks.asLiveData()
}
