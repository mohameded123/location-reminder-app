package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.locationreminders.savereminder.getOrAwaitValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    lateinit var v : RemindersListViewModel
    lateinit var r  : FakeDataSource
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()



    @Before
    fun  v()
    {
        r = FakeDataSource()
        v = RemindersListViewModel(ApplicationProvider.getApplicationContext(),r)

        stopKoin()


    }

    @Test
    fun loadReminders()
    {
        var  t =ReminderDTO("dsd" , "Sdsd" , "dfdf" ,55.0, 545.0)


        runBlockingTest {
            r.saveReminder(t)
            v.loadReminders()
            assertEquals(
                v.remindersList.getOrAwaitValue()[0].id , t.id)
            assertEquals(
                v.remindersList.getOrAwaitValue()[0].description , t.description)
            assertEquals(
                v.remindersList.getOrAwaitValue()[0].location, t.location)
            assertEquals(
                v.remindersList.getOrAwaitValue()[0].latitude , t.latitude)
            assertEquals(
                v.remindersList.getOrAwaitValue()[0].longitude , t.longitude)
        }


    }

    @Test
    fun loadReminderserror()
    {
            r.setReturnError(true)
            v.loadReminders()

        assertEquals(v.showNoData.getOrAwaitValue ( ), true )

       MatcherAssert.assertThat(v.showSnackBar.getOrAwaitValue(), (Matchers.`is`("error" )))



    }

}

@ExperimentalCoroutinesApi
class MainCoroutineRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()):
    TestWatcher(),
    TestCoroutineScope by TestCoroutineScope(dispatcher) {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}