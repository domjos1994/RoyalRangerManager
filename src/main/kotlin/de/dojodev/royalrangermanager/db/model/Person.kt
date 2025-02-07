package de.dojodev.royalrangermanager.db.model

import java.util.*


data class Person(
    var id: Int = 0,
    var memberId: String,
    var firstName: String,
    var middleName: String = "",
    var lastName: String,
    var childNumber: Int = 1,
    var gender: Int,
    var birthDate: Date?,
    var entryDate: Date?,
    var notes: String = "",
    var description: String = "",
    var medicines: String = "",
    var email: String = "",
    var phone: String = "",
    var street: String = "",
    var number: String = "",
    var locality: String = "",
    var postalCode: String = "",
    var ageGroupId: Int = 0,
    var teamId: Int = 0
) {
    val emergencyContacts = mutableListOf<EmergencyContact>()
}
