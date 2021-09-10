package ru.andreikud.mvvmtodo.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val tasksDao: TaskDao,
    private val scope: CoroutineScope
): ViewModel() {

    fun onDeletionConfirmed() = scope.launch {
        tasksDao.deleteAllCompleted()
    }
}