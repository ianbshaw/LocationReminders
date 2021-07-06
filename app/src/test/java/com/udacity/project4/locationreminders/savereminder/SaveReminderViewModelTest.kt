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
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var datasource: FakeDataSource

    private val reminderTitle = MutableLiveData<String>()
    private val reminderDescription = MutableLiveData<String>()
    private val reminderSelectedLocationStr = MutableLiveData<String>()
    private val selectedPOI = MutableLiveData<PointOfInterest>()
    private val latitude = MutableLiveData<Double>()
    private val longitude = MutableLiveData<Double>()

    private val reminders = listOf<ReminderDTO>()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupVM() {
        reminderTitle.value = "TITLE1"
        reminderDescription.value = "DESC1"
        reminderSelectedLocationStr.value = "TEST"
        latitude.value = 35.26765276556653
        longitude.value = -106.64450596604965
        val home = LatLng(latitude.value!!, longitude.value!!)
        selectedPOI.value = PointOfInterest(home, "Test1", "Test2")

        datasource = FakeDataSource(reminders.toMutableList())
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            datasource)
    }

    @Test
    fun onClear_removeAttrs() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        latitude.value = null
        longitude.value = null
        selectedPOI.value = null

        Assert.assertThat(reminderTitle.value, IsEqual(saveReminderViewModel.reminderTitle.value))
        Assert.assertThat(reminderDescription.value, IsEqual(saveReminderViewModel.reminderDescription.value))
        Assert.assertThat(reminderSelectedLocationStr.value, IsEqual(saveReminderViewModel.reminderSelectedLocationStr.value))
        Assert.assertThat(latitude.value, IsEqual(saveReminderViewModel.latitude.value))
        Assert.assertThat(longitude.value, IsEqual(saveReminderViewModel.longitude.value))
        Assert.assertThat(selectedPOI.value, IsEqual(saveReminderViewModel.selectedPOI.value))

    }

}