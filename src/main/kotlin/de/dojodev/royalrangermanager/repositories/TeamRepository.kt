package de.dojodev.royalrangermanager.repositories

import de.dojodev.royalrangermanager.db.mapper.AgeGroupMapper
import de.dojodev.royalrangermanager.db.mapper.TeamMapper
import de.dojodev.royalrangermanager.db.model.AgeGroup
import de.dojodev.royalrangermanager.db.model.Team
import de.dojodev.royalrangermanager.helper.DBHelper
import de.dojodev.royalrangermanager.helper.FXHelper
import org.apache.ibatis.session.SqlSession
import kotlin.jvm.Throws

class TeamRepository(
    private val sqlSession: SqlSession? = DBHelper.getSession()
) {
    private var teamMapper: TeamMapper? = null
    private var ageGroupMapper: AgeGroupMapper? = null

    init {
        this.teamMapper = this.sqlSession?.getMapper(TeamMapper::class.java)
        this.ageGroupMapper = this.sqlSession?.getMapper(AgeGroupMapper::class.java)
    }

    @Throws(Exception::class)
    fun getAgeGroups(): List<AgeGroup> {
        return this.ageGroupMapper?.getAgeGroups() ?: listOf()
    }

    @Throws(Exception::class)
    fun getTeams(): List<Team> {
        return this.teamMapper?.getTeams() ?: listOf()
    }

    @Throws(Exception::class)
    fun insertOrUpdateTeam(team: Team): Int {

        if(team.name.isEmpty()) {
            throw Exception(FXHelper.getBundle().getString("sys.team.dataNameNotNull"))
        }

        // load old teams
        val oldTeams = this.getTeams()

        if(team.id != 0) {

            // load old data
            this.teamMapper?.getTeam(team.id) ?: throw Exception(FXHelper.getBundle().getString("sys.team.dataNot"))

            val count = oldTeams.count { it.name == team.name && it.id != team.id }
            if(count >= 1) {
                throw Exception(FXHelper.getBundle().getString("sys.team.dataName"))
            }
        }

        if(!(team.gender == 0 || team.gender == 1 || team.gender == 2)) {
            throw Exception(FXHelper.getBundle().getString("sys.team.dataGender"))
        }

        if(this.getAgeGroups().count { it.id == team.ageGroupId } == 0) {
            throw Exception(FXHelper.getBundle().getString("sys.team.dataAgeGroup"))
        }

        if(team.id != 0) {
            this.teamMapper?.updateTeam(team)
            return team.id
        } else {
            return this.teamMapper?.insertTeam(team) ?: 0
        }
    }

    @Throws(Exception::class)
    fun deleteTeam(team: Team) {
        this.teamMapper?.deleteTeam(team)
    }
}