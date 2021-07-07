package com.udacity.project4.locationreminders.savereminder

import MainCoroutineRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNull.nullValue
import org.hamcrest.text.IsEmptyString.isEmptyOrNullString
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var datasource: FakeDataSource

    private val reminders = listOf<ReminderDTO>()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupVM() {
        datasource = FakeDataSource(reminders.toMutableList())
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            datasource)
    }

    @Test
    fun onClear_removeAttrs() {
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