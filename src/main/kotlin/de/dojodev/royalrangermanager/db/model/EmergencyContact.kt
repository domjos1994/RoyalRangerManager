package de.dojodev.royalrangermanager.db.model

data class EmergencyContact(
    var id: Int,
    var name: String,
    var email: String = "",
    var phone: String = ""
)