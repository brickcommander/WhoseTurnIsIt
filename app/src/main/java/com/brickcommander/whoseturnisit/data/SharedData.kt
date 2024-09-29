package com.brickcommander.whoseturnisit.data

import com.brickcommander.whoseturnisit.model.Person
import com.brickcommander.whoseturnisit.model.Work

object SharedData {
    var username: String = "UserName"
    var pendingWorkList: MutableList<Work> = mutableListOf()
    var personList: MutableList<Person> = mutableListOf()
}
