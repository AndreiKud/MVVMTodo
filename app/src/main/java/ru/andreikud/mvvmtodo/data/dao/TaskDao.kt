package ru.andreikud.mvvmtodo.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.andreikud.mvvmtodo.data.model.Task
import ru.andreikud.mvvmtodo.data.SortOrder

@Dao
interface TaskDao {

    fun query(filterStr: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> querySorted(filterStr, "dateCreatedInMs", hideCompleted)
            SortOrder.BY_NAME -> querySorted(filterStr, "name", hideCompleted)
        }

    @Query(
        """
           SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) 
                AND name LIKE '%' || :filterStr || '%' ORDER BY isImportant DESC, 
                CASE (:sortColumn)
                    WHEN 'dateCreatedInMs' then dateCreatedInMs
                    WHEN 'name' then name
                END
        """
    )
    fun querySorted(filterStr: String, sortColumn: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE isCompleted = 1")
    suspend fun deleteAllCompleted()
}