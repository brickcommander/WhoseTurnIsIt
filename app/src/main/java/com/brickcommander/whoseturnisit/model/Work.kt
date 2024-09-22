package com.brickcommander.whoseturnisit.model

import java.time.LocalDate

class Work {
    private var currentDate: LocalDate = LocalDate.now()
        get() = field
        set(value) {
            field = value
        }

    private var originalTurn: String = ""
        get() = field
        set(value) {
            field = value
        }

    private var whoseTurnIsIt: String = "Default=Yashwant"
        get() = field
        set(value) {
            field = value
        }

    private var whoWillEat: List<String> = listOf("Anmol", "Yashwant", "Satyam", "Pawan")
        get() = field
        set(value) {
            field = value
        }

    private var isFoodCooked: Boolean = true
        get() = field
        set(value) {
            field = value
        }

    private var workDone: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    override fun toString(): String {
        var res = """Work[currentDate=${currentDate}, originalTurn=${originalTurn}, whoseTurnIsIt=${whoseTurnIsIt}, whoWillEat=${whoWillEat}, isFoodCooked=${isFoodCooked}, workDone=${workDone}]"""
        return res
    }
}
