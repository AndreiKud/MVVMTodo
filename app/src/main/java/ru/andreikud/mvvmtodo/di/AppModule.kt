package ru.andreikud.mvvmtodo.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.andreikud.mvvmtodo.data.db.TaskDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context,
        callback: TaskDatabase.Callback
    ) = Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    @Singleton
    fun provideTaskDao(
        db: TaskDatabase
    ) = db.getDao()

    @Provides
    @Singleton
    fun provideAppScope() = CoroutineScope(SupervisorJob())
}