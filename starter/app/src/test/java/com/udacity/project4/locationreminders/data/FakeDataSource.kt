package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var v: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
private var shouldReturnError = false
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        if (shouldReturnError)
        {  return Result.Error("error" ,0)}
        return Result.Success(v)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
       v.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError)
        {  return Result.Error("error" ,0)}
       var  l = v.find { it.id == id }
       if (l == null  )
           return Result.Error("not found item" ,0)
        else
            return Result.Success(l)

    }

    override suspend fun deleteAllReminders() {
        v= mutableListOf()

    }


}