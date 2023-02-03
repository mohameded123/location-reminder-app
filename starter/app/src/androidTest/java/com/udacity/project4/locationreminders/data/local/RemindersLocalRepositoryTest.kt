package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class  RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private  lateinit var repo : RemindersLocalRepository
    @Before
    fun initDb() {

        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),RemindersDatabase::class.java).build()
        repo = RemindersLocalRepository(database.reminderDao() , Dispatchers.Unconfined)

    }
    @After
    fun closeDb() = database.close()


    @Test
    fun getReminders()
    {
        var r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )

        var r2 = ReminderDTO("dfdfvvcvcv " ,"ww" , "bvbvbv" , 555.0 ,32324.0 )
        //var  b : List<ReminderDTO>  = mutableListOf()
        runBlockingTest {
            repo.saveReminder(r1)
            repo.saveReminder(r2)

            var b = repo.getReminders()
            Assert.assertEquals(b,Result.Success(mutableListOf(r1 , r2)))
        }

    }



    @Test
    fun getReminderById()
    {
        var r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )

        var r : ReminderDTO ? = null
        runBlockingTest {
           repo.saveReminder(r1)

            assertEquals(repo.getReminder(r1.id) , Result.Success(r1))

            assertEquals(repo.getReminder("dssds") , Result.Error("Reminder not found!"))

        }



    }





    @Test
    fun deleteAllReminders()
    {
        var r1 = ReminderDTO("dsddf " ,"dsdsdsdsds" , "sdsvcv" , 5.0 ,22.0 )

        var r2 = ReminderDTO("dfdfvvcvcv " ,"ww" , "bvbvbv" , 555.0 ,32324.0 )

        runBlockingTest {
            repo.saveReminder(r1)
            repo.saveReminder(r2)

            database.reminderDao().deleteAllReminders()


            Assert.assertEquals(repo.getReminders(),Result.Success(mutableListOf<ReminderDTO>()))
        }


    }

}