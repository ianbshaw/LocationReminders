package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.locationreminders.RemindersActivity
import androidx.test.uiautomator.*
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var application: Application

    @Before
    fun init() {
        application = getApplicationContext()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    @Test
    fun login() {
        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        val device = UiDevice.getInstance(getInstrumentation())
        device.waitForIdle(1_000)

        onView(withId(R.id.auth_button)).perform(click())

        onView(withText(R.string.fui_sign_in_with_email)).perform(click())

        onView(withId(R.id.email)).perform(typeText("test1@test.com"), closeSoftKeyboard())
        onView(withId(R.id.button_next)).perform(click())

        val emailText: UiObject = device.findObject(UiSelector().text("test1@test.com"))
        emailText.waitUntilGone(2_000)
        device.waitForIdle()

        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.button_done)).perform(click())

        val reminderListFragmentStr = ReminderListFragment::class.java.`package`?.name
        device.wait(Until.hasObject(By.pkg(reminderListFragmentStr)), 3_000)

        val noDataStr = application.getString(R.string.no_data)
        onView(withId(R.id.noDataTextView)).check(matches(withText(noDataStr)))

        activityScenario.close()
    }

    @Test
    fun saveNewReminder() {
        runBlocking {
            val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
            dataBindingIdlingResource.monitorActivity(activityScenario)
            val activity = getActivity(activityScenario)

            onView(withId(R.id.addReminderFAB)).perform(click())

            val title = "TITLE"
            val description = "DESCRIPTION"

            onView(withId(R.id.reminderTitle)).perform(typeText(title), closeSoftKeyboard())
            onView(withId(R.id.reminderDescription)).perform(typeText(description), closeSoftKeyboard())
            onView(withId(R.id.selectLocation)).perform(click())

            val device = UiDevice.getInstance(getInstrumentation())
            val selectLocationFragmentPkgName = SelectLocationFragment::class.java.`package`?.name
            device.wait(Until.hasObject(By.pkg(selectLocationFragmentPkgName)), 3_000)

            val x = device.displayWidth / 2
            val y = device.displayHeight / 2
            device.swipe(x, y, x, y, 400)

            val selectLocationStr = getActivity(activityScenario)!!.getString(R.string.select_location)
            device.wait(Until.findObject(By.text(selectLocationStr).clickable(true)), 1_000)

            onView(withId(R.id.select_location_button)).perform(click())

            val saveReminderFragmentPkgName = SaveReminderFragment::class.java.`package`?.name
            device.wait(Until.hasObject(By.pkg(saveReminderFragmentPkgName)), 3_000)

            onView(withId(R.id.saveReminder)).perform(click())

            onView(withText(R.string.reminder_saved)).inRoot(withDecorView(not(activity?.window?.decorView)))
                .check(matches(isDisplayed()))

            activityScenario.close()
        }
    }

/*    @Test
    fun selectLocationFragment_doubleUpButton() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())

        onView(withId(R.id.selectLocation)).perform(click())

        onView(withId(R.id.selectLocationFragment)).perform(pressBack())
        onView(withId(R.id.reminderTitle)).check(matches(isDisplayed()))

        onView(withId(R.id.saveReminderFragment)).perform(pressBack())
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

        activityScenario.close()
    }*/

    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

}
