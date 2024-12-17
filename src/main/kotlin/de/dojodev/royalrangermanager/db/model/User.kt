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
) {

    fun isAdministrator(): Boolean {
        return admin == 1
    }

    fun isTrunkLeadership(): Boolean {
        return this.isAdministrator() || trunkLeader == 1 || trunkWait == 1 || trunkHelper == 1
    }

    fun isSeniorLeader(): Boolean {
        return this.isTrunkLeadership() || leader == 1
    }
}
