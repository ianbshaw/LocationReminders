package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.MainCoroutineRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var datasource: FakeDataSource

    private val reminder1 = ReminderDTO("Title1", "Description1", "Test1", 50.0, 50.0, "1")
    private val reminder2 = ReminderDTO("Title2", "Description2", "Test2", 50.0, 50.0, "2")
    private val reminder3 = ReminderDTO("Title3", "Description3", "Test3", 50.0, 50.0, "3")
    private val reminders = listOf(reminder1, reminder2, reminder3)

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupVM() {
        datasource = FakeDataSource(reminders.toMutableList())
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),
            datasource)
    }

    @Test
    fun loadReminders_setReminderList() {

        //Given
        val reminderData1 = ReminderDataItem("Title1", "Description1", "Test1", 50.0, 50.0, "1")
        val reminderData2 = ReminderDataItem("Title2", "Description2", "Test2", 50.0, 50.0, "2")
        val reminderData3 = ReminderDataItem("Title3", "Description3", "Test3", 50.0, 50.0, "3")
        val listData = listOf(reminderData1, reminderData2, reminderData3)

        //When loading reminders
        remindersListViewModel.loadReminders()
        val reminderList = remindersListViewModel.remindersList.value

        //Then viewmodel list equal to loaded list
        assertThat(reminderList, IsEqual(listData))

    }

    @Test
    fun loadStatisticsWhenRemindersAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors.
        datasource.setReturnError(true)
        runBlocking { datasource.refreshReminders() }
        remindersListViewModel.loadReminders()

        // Then empty and error are true (which triggers an error message to be shown).
        assertThat(remindersListViewModel.empty.getOrAwaitValue(), `is`(true))
        assertThat(remindersListViewModel.error.getOrAwaitValue(), `is`(true))
    }

}