package com.brickcommander.whoseturnisit.model

import java.time.LocalDate

class Person(private val name: String) {
    private var score: Int
    private var lastWorkingDay: LocalDate

    init {
        this.score = 0
        this.lastWorkingDay = LocalDate.now()
    }

    fun getName(): String {
        return name
    }

    fun getScore(): Int {
        return score
    }

    fun getLastWorkingDay(): LocalDate {
        return lastWorkingDay
    }

    fun setScore(score: Int): Int {
        this.score = score
        return score
    }

    fun increaseScore(increaseBy: Int): Int {
        score += increaseBy;
        return score
    }

    fun decreaseScore(): Int {
        score -= 12;
        return score
    }

    fun setLastWorkingDay(lastWorkingDay: LocalDate): LocalDate {
        this.lastWorkingDay = lastWorkingDay
        return lastWorkingDay
    }

    fun updateLastWorkingDay(): LocalDate {
        this.lastWorkingDay = LocalDate.now()
        return lastWorkingDay
    }

    override fun toString(): String {
        var res = """Person[Name=${name}, Score=${score}, LastWorkingDay=${lastWorkingDay}]"""
        return res
    }
}
