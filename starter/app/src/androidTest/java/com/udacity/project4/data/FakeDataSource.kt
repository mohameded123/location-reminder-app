package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var v: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
      v?.let {  return com.udacity.project4.locationreminders.data.dto.Result.Success(v)}
        return com.udacity.project4.locationreminders.data.dto.Result.Error("fd" ,0)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
       v.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
       var  l = v.find { it.id == id }
       if (l == null  )
           return com.udacity.project4.locationreminders.data.dto.Result.Error("fdfd" ,0)
        else
            return com.udacity.project4.locationreminders.data.dto.Result.Success(l)

    }

    override suspend fun deleteAllReminders() {
        v= mutableListOf()

    }


}