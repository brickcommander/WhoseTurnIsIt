package com.brickcommander.whoseturnisit.logic

import GitHubJsonHandler
import android.util.Log
import com.brickcommander.whoseturnisit.data.CONSTANTS
import com.brickcommander.whoseturnisit.model.Day
import com.brickcommander.whoseturnisit.model.Person
import com.brickcommander.whoseturnisit.model.Work

class Calculate() {

    private val handler = GitHubJsonHandler()

    companion object {
        const val TAG = "Calculate"
    }

    private fun canAddWorkInHistory(history: List<Work>, cacheWork: Work): Boolean {
        Log.i(TAG, "canAddWorkInHistory : history=$history : cacheWork=$cacheWork")
        history.forEach { work ->
            if (work.getDay() == cacheWork.getDay() && work.getCreatedDate() == cacheWork.getCreatedDate()) return false
        }
        return true
    }

    private fun getCacheWorkIndex(day: Day): Triple<MutableList<Work>, Int, Boolean> {
        Log.i(TAG, "getCacheWork : day=$day")
        val cacheWorkList = handler.getCacheWorkList() ?: throw Exception("CacheWorkList is null")

        cacheWorkList.forEachIndexed { index, work ->
            if (work.getDay() == day) { // TODO: check if date check is necessary
                return Triple(cacheWorkList, index, false)
            }
        }

        cacheWorkList.add(Work(day))
        return Triple(cacheWorkList, cacheWorkList.size - 1, true)
    }

    private fun updateScoreHistoryAndCacheWorkList(
        index: Int,
        cacheWorkList: MutableList<Work>,
        updateScore: Boolean
    ): String {
        Log.i(
            TAG,
            "updateScoreHistoryAndCacheWorkList : cacheWorkList=$cacheWorkList : index=$index : updateScore=$updateScore"
        )

        val history = handler.getWorkHistory() ?: throw Exception("History is null")
        if (canAddWorkInHistory(history, cacheWorkList[index])) {
            history.add(cacheWorkList[index])
            if (!handler.updateWorkHistory(history))
                return "Update History API Failure"

            // TODO: Handle the case when score gets updated but cacheWorkList is not updated
            if (updateScore) {
                val cacheWork = cacheWorkList[index]
                val personList = handler.getPersonList() ?: throw Exception("Persons List is null")
                val increaseScoreBy = 12 / cacheWork.getEatersList().size

                personList.forEach { person ->
                    // increase the score of eaters
                    if (person.getName() in cacheWork.getEatersList()) {
                        person.increaseScore(increaseScoreBy)
                    }

                    // reduce the score of washer by 12
                    if (person.getName() == cacheWork.getWhoWashed()) {
                        person.decreaseScore()
                        person.setLastWorkingDate(cacheWork.getCreatedDate())
                        person.setDay(cacheWork.getDay())
                    }
                }
                Log.i(TAG, "updateScoreHistoryAndCacheWorkList : new personList=$personList")

                if (!handler.updatePersonList(personList))
                    return "updatePersonList API Failure. Might Cause a Major Discrepancy in DB. Please inform the Developer."
            }
        }

        cacheWorkList.removeAt(index)
        if (!handler.updateCacheWorkList(cacheWorkList))
            return "updateCacheWorkList API Failure"

        return "Success"
    }

    fun declareFoodCooking(day: Day): String {
        Log.i(TAG, "declareFoodCooking : day=$day")
        val x = getCacheWorkIndex(day)
        val cacheWorkList = x.first
        val needToUpdate = x.third

        if (needToUpdate && !handler.updateCacheWorkList(cacheWorkList))
            return "updateCacheWorkList API Failure"

        return "Success"
    }

    fun declareWashed(name: String, day: Day): String {
        Log.i(TAG, "declareWashed : day=$day : name=$name")
        val x = getCacheWorkIndex(day)
        val cacheWorkList = x.first
        val index = x.second

        cacheWorkList[index].setWhoWashed(name)
        return updateScoreHistoryAndCacheWorkList(index, cacheWorkList, true)
    }

    fun whoseTurnIsIt(day: Day): String {
        Log.i(TAG, "whoseTurnIsIt : day=$day")
        val personList = handler.getPersonList() ?: throw Exception("Persons List is null")
        var resPerson: Person? = null

        var eatersList = CONSTANTS.namesInList
        var unableToWashList: List<String> = listOf()
        val cacheWorkList = handler.getCacheWorkList() ?: throw Exception("CacheWorkList is null")
        if(cacheWorkList.isNotEmpty()) {
            cacheWorkList.forEach { work ->
                if (work.getDay() == day) {
                    unableToWashList = work.getUnableToWash()
                    eatersList = work.getEatersList()
                }
            }
        }
        Log.i( TAG, "whoseTurnIsIt : cacheWorkList=$cacheWorkList : unableToWashList=$unableToWashList : personList=$personList")

        personList.forEach { person ->
            Log.i(TAG, "whoseTurnIsIt : person=$person")
            if ((resPerson == null
                        || person.getScore() > resPerson!!.getScore()
                        || (person.getScore() == resPerson!!.getScore()
                            && person.getLastWorkingDate().isBefore(resPerson!!.getLastWorkingDate()))
                        || (person.getScore() == resPerson!!.getScore()
                            && person.getLastWorkingDate().isEqual(resPerson!!.getLastWorkingDate())
                            && person.getDay().value < resPerson!!.getDay().value)
                )
                && (person.getName() !in unableToWashList)
                && (person.getName() in eatersList)
            ) {
                resPerson = person
            }
        }
        Log.i(TAG, "whoseTurnIsIt : resPerson=$resPerson")

        if (resPerson == null)
            return "No Valid Person Available"
        else
            return resPerson!!.getName()
    }

    fun declareNotEating(name: String, day: Day): String {
        Log.i(TAG, "declareNotEating : day=$day : name=$name")
        val x = getCacheWorkIndex(day)
        val cacheWorkList = x.first
        val index = x.second

        val needToUpdate = cacheWorkList[index].updateEatersList(name)
        if (needToUpdate && !handler.updateCacheWorkList(cacheWorkList))
            return "updateCacheWorkList API Failure"
        else
            return "Success"
    }

    fun declareCantWash(name: String, day: Day): String {
        Log.i(TAG, "declareCantWashToday : name=$name, day=$day")
        val x = getCacheWorkIndex(day)
        val cacheWorkList = x.first
        val index = x.second

        val needToUpdate = cacheWorkList[index].setUnableToWash(name)
        if (needToUpdate && !handler.updateCacheWorkList(cacheWorkList))
            return "updateCacheWorkList API Failure"
        else
            return "Success"
    }

    fun getPersonStatus(): List<Person> {
        Log.i(TAG, "getPersonStatus...")
        val personList = handler.getPersonList() ?: throw Exception("Persons List is null")
        return personList
    }

    fun getPendingWork(): List<Work> {
        Log.i(TAG, "getPendingWork...")
        val cacheWorkList = handler.getCacheWorkList() ?: throw Exception("CacheWorkList is null")
        return cacheWorkList
    }

    fun getPendingWorkDays(): Array<String> {
        Log.i(TAG, "getPendingWorkDays...")
        val cacheWorkList = handler.getCacheWorkList() ?: throw Exception("CacheWorkList is null")
        val pendingWorkDays = cacheWorkList.map { it.getDay().name }.toTypedArray()
        return pendingWorkDays
    }

    fun getHistory(): List<Work> {
        Log.i(TAG, "getHistory...")
        val historyList = handler.getWorkHistory() ?: throw Exception("History is null")
        return historyList
    }

}
