package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.ServiceLocator
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.FakeAndroidTestDataSource
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {


    private lateinit var dataSource: ReminderDataSource

    @Before
    fun initRepository() {
        dataSource = FakeAndroidTestDataSource()
        ServiceLocator.remindersLocalRepository = dataSource
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

/*    @Test
    fun clickLogoutButton_navigateToAuthenticationActivity() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        scenario.

        // WHEN - Click on the "logout" button
        onView(withId(R.id.logout)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )

        verify(startActivity(intent))
    }*/

    @Test
    fun clickAddReminderButton_navigateToSaveReminderFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun clickSaveReminderButton_navigateToRemindersListFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            SaveReminderFragmentDirections.actionSaveReminderFragmentToReminderListFragment()
        )
    }

    @Test
    fun clickSelectLocationButton_navigateToSelectLocationFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the location button
        onView(withId(R.id.selectLocation)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment()
        )
    }
}