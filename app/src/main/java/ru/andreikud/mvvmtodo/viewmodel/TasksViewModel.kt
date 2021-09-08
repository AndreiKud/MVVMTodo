package ru.andreikud.mvvmtodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    dao: TaskDao
) : ViewModel() {

    val tasks = dao.query().asLiveData()
}