package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    // runBlocking used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    @Test
    fun saveTask_retrievesTask() = runBlocking {
        // GIVEN - a new task saved in the database
        val reminder = ReminderDTO("title", "description", "location", 50.0, 50.0)
        localDataSource.saveReminder(reminder)

        // WHEN  - Task retrieved by ID
        val result = localDataSource.getReminder(reminder.id) as Result.Success

        // THEN - Same task is returned
        Assert.assertThat(result.data.title, `is`("title"))
        Assert.assertThat(result.data.description, `is`("description"))
        Assert.assertThat(result.data.location, `is`("location"))
        Assert.assertThat(result.data.latitude, `is`(50.0))
        Assert.assertThat(result.data.longitude, `is`(50.0))
    }

    @Test
    fun saveReminderAndDelete() = runBlocking {
        // Given a new task in the persistent repository
        val reminder = ReminderDTO("title", "description", "location", 50.0, 50.0)
        localDataSource.saveReminder(reminder)

        // When completed in the persistent repository
        localDataSource.deleteAllReminders()
        val result = localDataSource.getReminders() as Result.Success

        // Then the task can be retrieved from the persistent repository and is complete
        Assert.assertThat(result.data, `is`(emptyList()))
    }

}