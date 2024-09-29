package com.brickcommander.whoseturnisit.data

import com.brickcommander.whoseturnisit.BuildConfig

object CONSTANTS {
    val namesInArray = arrayOf("Pawan", "Satyam", "Anmol", "Yashwant")
    val namesInList = listOf("Pawan", "Satyam", "Anmol", "Yashwant")
    val pinArray = arrayOf(
        BuildConfig.PASSWORD_PAWAN,
        BuildConfig.PASSWORD_SATYAM,
        BuildConfig.PASSWORD_ANMOL,
        BuildConfig.PASSWORD_YASHWANT,
        BuildConfig.PASSWORD_MASTER
    )
    val pinToNameMap = mapOf(
        BuildConfig.PASSWORD_PAWAN to "Pawan",
        BuildConfig.PASSWORD_SATYAM to "Satyam",
        BuildConfig.PASSWORD_ANMOL to "Anmol",
        BuildConfig.PASSWORD_YASHWANT to "Yashwant",
        BuildConfig.PASSWORD_MASTER to "Master"
    )
    val userNameString = "username"
    val sharedPerferenceName = "usernameSharedPreference"
}
