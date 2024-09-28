package com.brickcommander.whoseturnisit.model

import java.time.LocalDate

class Person(private val name: String) {
    private var score: Int = 0
    private var lastWorkingDate: LocalDate
    private var day: String

    init {
        lastWorkingDate = LocalDate.now()
        day = Day.Default.name
    }

    fun getName(): String {
        return name
    }

    fun getScore(): Int {
        return score
    }

    fun getLastWorkingDate(): LocalDate {
        return lastWorkingDate
    }

    fun getDay(): Day {
        return Day.fromKey(day)
    }

    fun increaseScore(increaseBy: Int): Int {
        score += increaseBy;
        return score
    }

    fun decreaseScore(): Int {
        score -= 12;
        return score
    }

    fun setLastWorkingDate(date: LocalDate) {
        this.lastWorkingDate = date
    }

    fun setDay(day: Day) {
        this.day = day.name
    }

    override fun toString(): String {
        var res = """Person[Name=${name}, Score=${score}, LastWorkingDate=${lastWorkingDate}, Day=${day}]"""
        return res
    }
}
