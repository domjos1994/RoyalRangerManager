package de.dojodev.royalrangermanager.db.model

data class User(
    var id: Int = 0,
    var userName: String,
    var password: String,
    var gender: Int,
    var email: String,
    var admin: Int,
    var trunkLeader: Int,
    var trunkWait: Int,
    var trunkHelper: Int,
    var leader: Int,
    var juniorLeader: Int
)
