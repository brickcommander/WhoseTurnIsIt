package com.brickcommander.whoseturnisit.model

import java.time.LocalDate

class EatListCache {
    var currentDate: LocalDate = LocalDate.now()
    var whoWillEat: MutableList<String> = mutableListOf("Anmol", "Yashwant", "Satyam", "Pawan")

    override fun toString(): String {
        var res = """EatListCache[currentDate=${currentDate}, whoWillEat=${whoWillEat}]"""
        return res
    }
}
