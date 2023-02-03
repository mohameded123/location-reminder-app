package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    @Before
    fun initDb() {

     database = Room.inMemoryDatabaseBuilder(getApplicationContext() ,RemindersDatabase::class.java).build()
    }
    @After
    fun closeDb() = database.close()


    @Test
    fun getReminders()
    {
        var r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )

        var r2 = ReminderDTO("dfdfvvcvcv " ,"ww" , "bvbvbv" , 555.0 ,32324.0 )
        var  b : List<ReminderDTO>  = mutableListOf()
        runBlockingTest {
            database.reminderDao().saveReminder(r1)
            database.reminderDao().saveReminder(r2)

         b =    database.reminderDao().getReminders()
        }
        assertEquals(b , mutableListOf<ReminderDTO>(r1 ,r2))

    }

    @Test
    fun getReminderById()
    {
        var r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )

        var r : ReminderDTO ? = null
        runBlockingTest {
            database.reminderDao().saveReminder(r1)

           r=  database.reminderDao().getReminderById(r1.id)

        }

        assertThat(r ,Matchers.`is`(r1))

    }


    @Test
    fun deleteAllReminders()
    {
        var r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )

        var r2 = ReminderDTO("dfdfvvcvcv " ,"ww" , "bvbvbv" , 555.0 ,32324.0 )
        var  b : List<ReminderDTO>  = mutableListOf()
        runBlockingTest {
            database.reminderDao().saveReminder(r1)
            database.reminderDao().saveReminder(r2)

            database.reminderDao().deleteAllReminders()
            b =    database.reminderDao().getReminders()


        }
        assertEquals(b , mutableListOf<ReminderDTO>())

    }
}