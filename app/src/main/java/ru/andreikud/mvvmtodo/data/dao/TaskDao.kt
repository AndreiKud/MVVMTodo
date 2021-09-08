package ru.andreikud.mvvmtodo.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.andreikud.mvvmtodo.data.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :nameFilter || '%' ORDER BY dateCreatedInMs")
    fun query(nameFilter: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}