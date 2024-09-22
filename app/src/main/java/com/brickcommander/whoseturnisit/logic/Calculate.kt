package com.brickcommander.whoseturnisit.logic

import GitHubJsonHandler
import android.util.Log
import com.brickcommander.whoseturnisit.data.SharedData
import java.time.LocalDateTime

class Calculate() {

    // Create an instance of GitHubJsonHandler
    private val handler = GitHubJsonHandler()

    companion object {
        const val TAG = "Calculate"
    }

    fun settlePreviousDay() {
        Log.i(TAG, "Calculate settlePreviousDay...")

        val previousWork = SharedData.workList.last()
        Log.i(TAG, "getWhoseTurnIsIt(): work = $previousWork")

        val now = LocalDateTime.now()
        val today1830 = now.withHour(18).withMinute(30).withSecond(0).withNano(0)

        if (now.isAfter(today1830) && SharedData.cacheWork.getCurrentDate().isBefore(now.toLocalDate())
            && SharedData.cacheWork.isFoodCooked()
            && previousWork.getCurrentDate() != SharedData.cacheWork.getCurrentDate()) {
            handler.updateWorkListInDB(SharedData.cacheWork)
        }
    }

    init {
        updateToLatestDB();

        Log.i(TAG, "Calculate Constructor...")
        val work = SharedData.workList.last()
        Log.i(TAG, "getWhoseTurnIsIt(): work = $work")
        val now = LocalDateTime.now()
        val today1830 = now.withHour(18).withMinute(30).withSecond(0).withNano(0)
        val yesterday = now.minusDays(1).toLocalDate()

//        if ((now.isAfter(today1830) && t.toLocalDate().isBefore(now.toLocalDate()))
//            || (now.isBefore(today1830) && t.toLocalDate().isBefore(yesterday))) {
//            // The current time is past 18:30 and t.date is before today
//            // OR current time is before 18:30 and t.date is before yesterday
//
//
//
//        } else {
//            // The condition is not met
//
//        }
    }

    // Synchronous function to fetch JSON
    fun updateToLatestDB() {
        Log.i(TAG, "updateToLatestDB()")

        // Fetch data synchronously
        handler.fetchAllDataFromDB()
    }

    fun getWhoseTurnIsIt(): String {
        Log.i(TAG, "getWhoseTurnIsIt()")

        if(SharedData.workList.isNotEmpty()) {
            val work = SharedData.workList.lastOrNull()
            Log.i(TAG, "getWhoseTurnIsIt(): work = $work")
            return "Satyam"
        } else {
            return "NA"
        }
    }


}
