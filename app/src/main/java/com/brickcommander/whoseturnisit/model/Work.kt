package com.brickcommander.whoseturnisit.model

import java.time.LocalDate

class Work {
    private var currentDate: LocalDate = LocalDate.now()

    private var originalTurn: String = ""

    private var whoseTurnIsIt: String = "Default=Yashwant"

    private var whoWillEat: List<String> = listOf("Anmol", "Yashwant", "Satyam", "Pawan")

    private var isFoodCooked: Boolean = true

    private var workDone: Boolean = false

    fun getCurrentDate(): LocalDate {
        return currentDate
    }

    fun setCurrentDate(date: LocalDate) {
        this.currentDate = date
    }

    fun getOriginalTurn(): String {
        return originalTurn
    }

    fun setOriginalTurn(turn: String) {
        this.originalTurn = turn
    }

    fun getWhoseTurnIsIt(): String {
        return whoseTurnIsIt
    }

    fun setWhoseTurnIsIt(turn: String) {
        this.whoseTurnIsIt = turn
    }

    fun getWhoWillEat(): List<String> {
        return whoWillEat
    }

    fun setWhoWillEat(eaters: List<String>) {
        this.whoWillEat = eaters
    }

    fun isFoodCooked(): Boolean {
        return isFoodCooked
    }

    fun setFoodCooked(cooked: Boolean) {
        this.isFoodCooked = cooked
    }

    fun isWorkDone(): Boolean {
        return workDone
    }

    fun setWorkDone(done: Boolean) {
        this.workDone = done
    }

    override fun toString(): String {
        var res = """Work[currentDate=${currentDate}, originalTurn=${originalTurn}, whoseTurnIsIt=${whoseTurnIsIt}, whoWillEat=${whoWillEat}, isFoodCooked=${isFoodCooked}, workDone=${workDone}]"""
        return res
    }
}
