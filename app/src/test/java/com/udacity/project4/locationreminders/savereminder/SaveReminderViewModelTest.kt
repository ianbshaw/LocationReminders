package com.udacity.project4.locationreminders.savereminder

import com.udacity.project4.locationreminders.MainCoroutineRule
import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsNull.nullValue
import org.hamcrest.text.IsEmptyString.isEmptyOrNullString
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var datasource: FakeDataSource
    private lateinit var app: Application

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        app = getApplicationContext()
        datasource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(app, datasource)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun onClear_removeReminder() {
        saveReminderViewModel.apply {
            reminderTitle.value = "TITLE1"
            reminderDescription.value = "DESC1"
            reminderSelectedLocationStr.value = "LOC1"
            latitude.value = 35.26765276556653
            longitude.value = -106.64450596604965
            selectedPOI.value = PointOfInterest(LatLng(latitude.value!!, longitude.value!!), "ID", "NAME")
        }
        saveReminderViewModel.onClear()

        assertThat(saveReminderViewModel.reminderTitle.value, isEmptyOrNullString())
        assertThat(saveReminderViewModel.reminderDescription.value, isEmptyOrNullString())
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value, isEmptyOrNullString())
        assertThat(saveReminderViewModel.latitude.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.longitude.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.selectedPOI.value, `is`(nullValue()))
    }

    @Test
    fun saveReminder_addReminderToList() {
        mainCoroutineRule.pauseDispatcher()

        //When saving a reminder
        val reminder = ReminderDataItem(title, description, location, latitude, longitude)
        saveReminderViewModel.saveReminder(reminder)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`(app.getString(R.string.reminder_saved)))
/*
        runBlocking {
            val retrievedReminder = (datasource.getReminder(id) as Result.Success).data

            assertThat(retrievedReminder.title      , `is`(title      ))
            assertThat(retrievedReminder.description, `is`(description))
            assertThat(retrievedReminder.location   , `is`(location   ))
            assertThat(retrievedReminder.latitude   , `is`(latitude   ))
            assertThat(retrievedReminder.longitude  , `is`(longitude  ))
        }*/
    }

    @Test
    fun validateEnteredData_titleIsEmpty() {
        val emptyTitle  = ""

        val noTitleReminder = ReminderDataItem(emptyTitle, description, location, latitude, longitude, id)
        saveReminderViewModel.validateEnteredData(noTitleReminder)

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_locationIsNull() {
        val nullLocation = null

        val noTitleReminder = ReminderDataItem(title, description, nullLocation, latitude, longitude, id)
        saveReminderViewModel.validateEnteredData(noTitleReminder)

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
    }

    companion object {
        const val title       = "TITLE"
        const val description = "DESCRIPTION"
        const val location    = "LOCATION"
        const val latitude    = 1.111
        const val longitude   = 2.222
        const val id          = "UUID"
    }

}