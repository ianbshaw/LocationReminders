package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        reminders?.let { return Success(ArrayList(it)) }
        return Result.Error(
            Exception("Reminders not found").toString()
        )
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        reminders?.forEach {
            if (it.id == id) {
                return Success(it)
            }
        }
        return Result.Error (
            Exception("No reminder with id found").toString()
        )
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}