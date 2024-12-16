package de.dojodev.royalrangermanager.db.model

import java.util.*


data class Person(
    var id: Int = 0,
    var firstName: String,
    var middleName: String = "",
    var lastName: String,
    var gender: Int,
    var birthDate: Date,
    var notes: String = "",
    var description: String = "",
    var medicine: String = "",
    var email: String = "",
    var phone: String = "",
    var ageGroupId: Int = 0,
    var teamId: Int = 0
)
