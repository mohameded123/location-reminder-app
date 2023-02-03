package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand

import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.MainCoroutineRule
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects
    lateinit var v : SaveReminderViewModel
    lateinit var r  : FakeDataSource
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @Before
fun  v()
{
    r = FakeDataSource()
    v = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),r)

    stopKoin()

}

    @Test
    fun saveReminder()
    {
        var re  = ReminderDataItem("dsd" , "Sdsd" , "dfdf" ,55.0, 545.0)

        mainCoroutineRule.pauseDispatcher()
        v.saveReminder(re)
        assertThat(v.showLoading.getOrAwaitValue() , (`is`(true)))

        mainCoroutineRule.resumeDispatcher()
        assertThat(v.showLoading.getOrAwaitValue() , (`is`(false)))

        assertThat(v.showToast.getOrAwaitValue() , (`is`( v.app.getString(R.string.reminder_saved))))



    }
    @Test
    fun validateEnteredData()
    {
        var re1  = ReminderDataItem("dsd" , "Sdsd" , "Dfdfdf" ,55.0, 545.0)
        var re2  = ReminderDataItem(null , "null" , null ,55.0, 545.0)




            var a =   v.validateEnteredData(re1)

        assertThat(a , (`is`(true) ))

          var   b = v.validateEnteredData(re2)
        assertThat(b , (`is`(false) ))


            var c = v.showSnackBarInt.getOrAwaitValue ()




        assertThat(c , (`is`(R.string.err_enter_title)) )



    }




}
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}