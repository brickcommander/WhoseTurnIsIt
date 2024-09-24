package com.brickcommander.whoseturnisit.logic

import GitHubJsonHandler
import android.util.Log
import com.brickcommander.whoseturnisit.data.SharedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class Calculate() {

    // Create an instance of GitHubJsonHandler
    private val handler = GitHubJsonHandler()

    companion object {
        const val TAG = "Calculate"
    }

    private fun settlePreviousDay() {
        Log.i(TAG, "settlePreviousDay...")

        val previousWork = SharedData.workList.last()
        val cacheWork = SharedData.cacheWork
        Log.i(TAG, "settlePreviousDay(): work = $previousWork")

        val now = LocalDateTime.now()
        val today1830 =
            now.withHour(18).withMinute(30).withSecond(0).withNano(0) // 6:30PM current day
        val yesterday = now.minusDays(1).toLocalDate()

        if ((now.isAfter(today1830)
                    && cacheWork.getCurrentDate().isBefore(now.toLocalDate())
                    || cacheWork.getCurrentDate().isBefore(yesterday)
                    ) && cacheWork.isFoodCooked()
            && previousWork.getCurrentDate() != cacheWork.getCurrentDate()
            && previousWork.isWorkDone()
        ) {
            handler.updateWorkListInDB(cacheWork)
            updateScoreOfPersons()
        }
    }

    private fun updateScoreOfPersons() {
        Log.i(TAG, "updateScoreOfPersons...")

        val cacheWork = SharedData.cacheWork

        // update the score of each person in Persons List
        val score = 12 / cacheWork.getWhoWillEat().size
        val currPersonsList = SharedData.personsList.toMutableList()
        for (person in cacheWork.getWhoWillEat()) {
            for (p in currPersonsList) {
                if (p.getName() == person) {
                    p.increaseScore(score)
                }
            }
        }

        // reduce the score of the person who washed the dishes and update the last working day
        for (p in currPersonsList) {
            if (p.getName() == cacheWork.getWhoseTurnIsIt()) {
                p.decreaseScore()
                p.updateLastWorkingDay(cacheWork.getCurrentDate())
            }
        }

        handler.updatePersonsListInDB(currPersonsList.toList())
    }

    suspend fun refresh() {
        Log.i(TAG, "refresh...")

        withContext(Dispatchers.IO) {
            // Fetch data synchronously
            handler.fetchAllDataFromDB()
            settlePreviousDay()

            val now = LocalDateTime.now()
            val today1830 =
                now.withHour(18).withMinute(30).withSecond(0).withNano(0) // 6:30PM current day
            if (now.isAfter(today1830) && SharedData.eatListCache.currentDate == SharedData.cacheWork.getCurrentDate()) {
                val cacheWorkTemp = SharedData.cacheWork
                cacheWorkTemp.setWhoWillEat(SharedData.eatListCache.whoWillEat.toList())
                handler.updateCacheWorkInDB(cacheWorkTemp)
            }
        }
    }

    init {
        Log.i(TAG, "Constructor...")
//        refresh()
    }

    suspend fun getWhoseTurnIsIt(): String {
        Log.i(TAG, "getWhoseTurnIsIt...")

        val previousWork = SharedData.workList.last()
        Log.i(TAG, "getWhoseTurnIsIt(): previousWork = $previousWork")

        val now = LocalDateTime.now()
        val today1830 =
            now.withHour(1).withMinute(30).withSecond(0).withNano(0) // 6:30PM current day

        var personName: String = "NA"
        if(now.isAfter(today1830)) {
            var lastWorkingDay = LocalDate.now()
            var maxScore = Int.MIN_VALUE
            SharedData.personsList.forEach {
                person -> if(SharedData.eatListCache.whoWillEat.contains(person.getName())
                            && (person.getScore() > maxScore
                                || person.getScore() == maxScore && person.getLastWorkingDay().isBefore(lastWorkingDay))) {
                    maxScore = person.getScore()
                    lastWorkingDay = person.getLastWorkingDay()
                    personName = person.getName()
                }
            }
            SharedData.cacheWork.setOriginalTurn(personName)
            SharedData.cacheWork.setWhoseTurnIsIt(personName)
            withContext(Dispatchers.IO) {
                handler.updateCacheWorkInDB(SharedData.cacheWork)
            }
        }

        return personName
    }

    suspend fun updateEatersList(name: String): Boolean {
        Log.i(TAG, "updateEatersList : name=$name")
        return withContext(Dispatchers.IO) {
            handler.updateEatListCacheInDB(name)
        }
    }

//    suspend fun updateFoodStatus(): Boolean {
//
//        return true
//    }

//    fun updateWasherStatus(): String {
//        val now = LocalDateTime.now()
//        val today1830 =
//            now.withHour(18).withMinute(30).withSecond(0).withNano(0) // 6:30PM current day
//        val today1200 = now.withHour(12).withMinute(0).withSecond(0).withNano(0) // 12:00PM current day
//
//        if(now.isAfter(today1200) && now.isBefore(today1830)) {
//            SharedData.cacheWork.
//        } else {
//            return "Please update between 1200 and 1830 Hours"
//        }
//    }

}
