package ru.andreikud.mvvmtodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    dao: TaskDao
) : ViewModel() {

    val queryNameFilter = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    private val filteredTasks = queryNameFilter.flatMapLatest { filterStr ->
        dao.query(filterStr)
    }

    @ExperimentalCoroutinesApi
    val tasks = filteredTasks.asLiveData()
}