package com.udacity.project4

import android.app.Application
import android.net.Uri
import android.os.Parcel
import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.ClassPathScanner.ClassNameFilter
import com.google.android.gms.internal.firebase_auth.zzes
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.android.synthetic.main.activity_reminders.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.ATTRIBUTE_VIEW_MODEL
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application


    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        repository =RemindersLocalRepository(LocalDB.createRemindersDao(getApplicationContext()))
        (appContext as MyApp).user = object : FirebaseUser()
        {
            override fun writeToParcel(dest: Parcel?, flags: Int) {
                TODO("Not yet implemented")
            }

            override fun getUid(): String {
                TODO("Not yet implemented")
            }

            override fun getProviderId(): String {
                TODO("Not yet implemented")
            }

            override fun getDisplayName(): String? {
                TODO("Not yet implemented")
            }

            override fun getPhotoUrl(): Uri? {
                TODO("Not yet implemented")
            }

            override fun getEmail(): String? {
                TODO("Not yet implemented")
            }

            override fun getPhoneNumber(): String? {
                TODO("Not yet implemented")
            }

            override fun isEmailVerified(): Boolean {
                TODO("Not yet implemented")
            }

            override fun isAnonymous(): Boolean {
                TODO("Not yet implemented")
            }

            override fun zzcw(): MutableList<String>? {
                TODO("Not yet implemented")
            }

            override fun getProviderData(): MutableList<out UserInfo> {
                TODO("Not yet implemented")
            }

            override fun zza(p0: MutableList<out UserInfo>): FirebaseUser {
                TODO("Not yet implemented")
            }

            override fun zza(p0: zzes) {
                TODO("Not yet implemented")
            }

            override fun zzcx(): FirebaseUser {
                TODO("Not yet implemented")
            }

            override fun zzcu(): FirebaseApp {
                TODO("Not yet implemented")
            }

            override fun zzba(): String? {
                TODO("Not yet implemented")
            }

            override fun zzcy(): zzes {
                TODO("Not yet implemented")
            }

            override fun zzcz(): String {
                TODO("Not yet implemented")
            }

            override fun zzda(): String {
                TODO("Not yet implemented")
            }

            override fun getMetadata(): FirebaseUserMetadata? {
                TODO("Not yet implemented")
            }

            override fun zzdb(): zzv {
                TODO("Not yet implemented")
            }

            override fun zzb(p0: MutableList<zzx>?) {
                TODO("Not yet implemented")
            }

        }

        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )

            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }
    private  var dataBindingIdlingResource = DataBindingIdlingResource()
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }


    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
//    TODO: add End to End testing to the app
@Test
fun editTask() = runBlocking {

    // Set initial state.
    var r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )
  //  repository.saveReminder(r1)

    // Start up Tasks screen.
    val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
    dataBindingIdlingResource.monitorActivity(activityScenario)


    onView(withId(R.id.addReminderFAB)).perform(click())

    onView(withId(R.id.saveReminder)).perform(click())



    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.err_enter_title)))


    onView(withId(R.id.reminderTitle)).perform(replaceText(r1.title))




    onView(withId(R.id.reminderDescription)).perform(replaceText(r1.description))

    onView(withId(R.id.selectLocation)).perform(click())
    onView(withId(R.id.map11)).perform(longClick())
    activityScenario.onActivity {
        it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()?.navigateUp()
    }


    onView(withId(R.id.saveReminder)).perform(click())
Thread.sleep(2000)
  onView(withText(R.string.reminder_saved)).inRoot(RootMatchers.withDecorView(not(getActivity(appContext)?.getWindow()?.getDecorView()))).check(matches(isDisplayed()))


    onView(withText(r1.title)).check(matches( isDisplayed()))



    activityScenario.close()
}


}
