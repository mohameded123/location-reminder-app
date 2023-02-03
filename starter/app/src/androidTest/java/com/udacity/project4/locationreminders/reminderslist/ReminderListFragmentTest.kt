package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.gms.common.api.internal.LifecycleFragment
import com.udacity.project4.MyApp
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

     var repo :FakeDataSource = FakeDataSource()
    @Before
    fun before() {
        stopKoin()
    val myModule = module {


        viewModel {
            RemindersListViewModel(
                get(),
                get() as FakeDataSource
            )
        }
        single { repo  }

    }
    startKoin {
        androidContext(getApplicationContext())
        modules(listOf(myModule))
    }


    }


    @Test
    fun v()
    {var  r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )
        runBlockingTest {
            repo.saveReminder(r1)
        }
          val s=    launchFragmentInContainer<ReminderListFragment>(null ,R.style.AppTheme )

        val nc = mock(NavController::class.java)
          s.onFragment {
              Navigation.setViewNavController(it.view!!, nc)
          }



          onView(withId(R.id.title)).check(matches(isDisplayed()))
          onView(withId(R.id.title)).check(matches(withText(r1.title)))
          onView(withId(R.id.description)).check(matches(withText(r1.description)))
          onView(withId(R.id.addReminderFAB)).perform(click())
          verify(nc).navigate(ReminderListFragmentDirections.toSaveReminder())






    }
    @Test
    fun b()
    {
        val s=    launchFragmentInContainer<ReminderListFragment>(null ,R.style.AppTheme )
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

    }


}