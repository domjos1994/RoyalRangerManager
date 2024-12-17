package de.dojodev.royalrangermanager.db.model

data class Team(
    var id: Int = 0,
    var name: String,
    var description: String = "",
    var note: String = "",
    var gender: Int,
    var ageGroupId: Int
) {

    override fun toString(): String {
        return this.name
    }
}
