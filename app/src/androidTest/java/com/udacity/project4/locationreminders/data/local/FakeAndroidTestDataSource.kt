package com.udacity.project4.locationreminders.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeAndroidTestDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    private val observableReminders = MutableLiveData<Result<List<ReminderDTO>>>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception").toString())
        }
        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(Exception("Reminders not found").toString())
    }

    suspend fun refreshReminders() {
        observableReminders.value = getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception").toString())
        }
        reminders?.forEach {
            if (it.id == id) {
                return Result.Success(it)
            }
        }
        return Result.Error(Exception("No reminder with id found").toString())
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun observeTasks(): LiveData<Result<List<ReminderDTO>>> {
        runBlocking { refreshReminders() }
        return observableReminders
    }

}