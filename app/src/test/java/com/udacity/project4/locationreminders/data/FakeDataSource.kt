package com.udacity.project4.locationreminders.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.Result.Error
import kotlinx.coroutines.runBlocking

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false

    private val observableReminders = MutableLiveData<Result<List<ReminderDTO>>>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception").toString())
        }
        reminders?.let { return Success(ArrayList(it)) }
        return Error(Exception("Reminders not found").toString())
    }

    suspend fun refreshReminders() {
        observableReminders.value = getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Error(Exception("Test exception").toString())
        }
        reminders?.forEach {
            if (it.id == id) {
                return Success(it)
            }
        }
        return Error(Exception("No reminder with id found").toString())
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