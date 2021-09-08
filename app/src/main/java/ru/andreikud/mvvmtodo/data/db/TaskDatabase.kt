package ru.andreikud.mvvmtodo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.data.dao.TaskDao
import ru.andreikud.mvvmtodo.data.model.Task
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getDao(): TaskDao

    class Callback @Inject constructor(
        private val taskDb: Provider<TaskDatabase>,
        private val coroutineAppScope: CoroutineScope,
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = taskDb.get().getDao()
            coroutineAppScope.launch {
                dao.insert(Task(name = "Learn Kotlin", isImportant = true))
                dao.insert(Task(name = "Learn Android", isImportant = true))
                dao.insert(Task(name = "Learn english", isImportant = true))
                dao.insert(Task(name = "Read books", isImportant = false, isCompleted = true))
                dao.insert(Task(name = "Call mom", isImportant = true, isCompleted = true))
                dao.insert(Task(name = "Find a girlfriend", isImportant = false))
                dao.insert(Task(name = "Quit smoking", isImportant = false))
            }
        }

    }

}