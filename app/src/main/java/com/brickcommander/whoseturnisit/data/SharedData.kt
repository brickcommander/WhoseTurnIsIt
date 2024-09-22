package com.brickcommander.whoseturnisit.data

import com.brickcommander.whoseturnisit.model.Person
import com.brickcommander.whoseturnisit.model.Work

object SharedData {
    var personsList: List<Person> = emptyList()
    var workList: List<Work> = emptyList()
}