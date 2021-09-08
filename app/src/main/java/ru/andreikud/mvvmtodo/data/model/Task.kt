package ru.andreikud.mvvmtodo.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var isImportant: Boolean,
    val name: String,
    val isCompleted: Boolean = false,
    val dateCreatedInMs: Long = System.currentTimeMillis()
) : Parcelable {
    val formattedTimeCreated: String
        get() = DateFormat.getTimeInstance().format(dateCreatedInMs)
}