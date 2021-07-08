package com.udacity.project4

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.runBlocking

/**
 * A Service Locator for the [TasksRepository]. This is the prod version, with a
 * the "real" [TasksRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: RemindersDatabase? = null
    @Volatile
    var remindersLocalRepository: ReminderDataSource? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): ReminderDataSource {
        synchronized(this) {
            return remindersLocalRepository ?: createRemindersRepository(context)
        }
    }

    private fun createRemindersRepository(context: Context): RemindersLocalRepository {
        val newRepo = RemindersLocalRepository(database!!.reminderDao())
        remindersLocalRepository = newRepo
        return newRepo
    }

    private fun createRemindersLocalRepository(context: Context): RemindersLocalRepository {
        val database = database ?: createDatabase(context)
        return RemindersLocalRepository(database.reminderDao())
    }

    private fun createDatabase(context: Context): RemindersDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            RemindersDatabase::class.java, "Reminders.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
              remindersLocalRepository?.deleteAllReminders()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            remindersLocalRepository = null
        }
    }
}
