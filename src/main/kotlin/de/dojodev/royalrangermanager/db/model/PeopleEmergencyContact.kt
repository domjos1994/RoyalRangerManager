package de.dojodev.royalrangermanager.db.model

data class PeopleEmergencyContact(
    var id: Int = 0,
    var personId: Int,
    var emergencyContactId: Int
)