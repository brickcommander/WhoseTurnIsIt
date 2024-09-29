package com.brickcommander.whoseturnisit.model

import com.brickcommander.whoseturnisit.data.CONSTANTS
import java.time.LocalDate

class Work(d: Day) {

    private val id: String = java.util.UUID.randomUUID().toString()
    private val createdDate: LocalDate = LocalDate.now()
    private val day: String = d.name
    private var eatersList: MutableList<String> = CONSTANTS.namesInList.toMutableList()
    private var unableToWash: MutableList<String> = mutableListOf()
    private var foodCooked: Boolean = true
    private var whoWashed: String = "NA"

    fun getId(): String {
        return id
    }

    fun getCreatedDate(): LocalDate {
        return createdDate
    }

    fun getDay(): Day {
        return Day.fromKey(day)
    }

    fun getEatersList(): MutableList<String> {
        return eatersList
    }

    fun updateEatersList(name: String): Boolean {
        return eatersList.remove(name)
    }

    fun getUnableToWash(): MutableList<String> {
        return unableToWash
    }

    fun setUnableToWash(name: String): Boolean {
        if(this.unableToWash.contains(name)) return false
        return this.unableToWash.add(name)
    }

    fun isFoodCooked(): Boolean {
        return foodCooked
    }

    fun getWhoWashed(): String {
        return whoWashed
    }

    fun setWhoWashed(washed: String) {
        this.whoWashed = washed
    }

    override fun toString(): String {
        return """Work[id=${id}, createdDate=${createdDate}, day=${day}, eatersList=${eatersList}, unableToWash=${unableToWash}, foodCooked=${foodCooked}, whoWashed=${whoWashed}]"""
    }

}
