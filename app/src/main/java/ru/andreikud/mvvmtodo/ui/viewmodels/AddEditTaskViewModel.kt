package ru.andreikud.mvvmtodo.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import ru.andreikud.mvvmtodo.data.model.Task
import javax.inject.Inject

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

    var isImportant =
        savedStateHandle.get<Boolean>("isImportant") ?: task?.isImportant ?: false
        set(value) {
            field = value
            savedStateHandle.set("isImportant", value)
        }
}