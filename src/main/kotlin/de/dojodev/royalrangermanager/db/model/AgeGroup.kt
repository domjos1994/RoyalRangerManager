package de.dojodev.royalrangermanager.db.model

data class AgeGroup(
    var id: Int = 0,
    var nameEn: String,
    var nameDe: String,
    var minAge: Int,
    var maxAge: Int
)
