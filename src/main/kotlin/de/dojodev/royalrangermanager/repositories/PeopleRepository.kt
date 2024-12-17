package de.dojodev.royalrangermanager.repositories

import de.dojodev.royalrangermanager.db.mapper.AgeGroupMapper
import de.dojodev.royalrangermanager.db.mapper.PeopleMapper
import de.dojodev.royalrangermanager.db.mapper.TeamMapper
import de.dojodev.royalrangermanager.db.model.AgeGroup
import de.dojodev.royalrangermanager.db.model.Person
import de.dojodev.royalrangermanager.db.model.Team
import de.dojodev.royalrangermanager.helper.DBHelper
import de.dojodev.royalrangermanager.helper.FXHelper
import org.apache.ibatis.session.SqlSession
import java.util.*
import kotlin.jvm.Throws

class PeopleRepository(
    private val sqlSession: SqlSession? = DBHelper.getSession()
) {
    private var teamMapper: TeamMapper? = null
    private var ageGroupMapper: AgeGroupMapper? = null
    private var peopleMapper: PeopleMapper? = null

    init {
        this.teamMapper = this.sqlSession?.getMapper(TeamMapper::class.java)
        this.ageGroupMapper = this.sqlSession?.getMapper(AgeGroupMapper::class.java)
        this.peopleMapper = this.sqlSession?.getMapper(PeopleMapper::class.java)
    }

    @Throws(Exception::class)
    fun getPeople(): List<Person> {
        return this.peopleMapper?.getPeople() ?: listOf()
    }

    @Throws(Exception::class)
    fun getTeams(): List<Team> {
        return this.teamMapper?.getTeams() ?: listOf()
    }

    @Throws(Exception::class)
    fun getAgeGroups(): List<AgeGroup> {
        return this.ageGroupMapper?.getAgeGroups() ?: listOf()
    }

    @Suppress("unused")
    @Throws(Exception::class)
    fun getPerson(id: Int): Person? {
        return this.peopleMapper?.getPerson(id)
    }

    @Throws(Exception::class)
    fun insertOrUpdatePerson(person: Person): Int {

        // load old data
        val oldPeople = this.peopleMapper?.getPeople()

        // person already exists
        if(person.id != 0) {
            val count = oldPeople?.count { it.id != person.id && (it.firstName == person.firstName && it.lastName == person.lastName) }
            if(count != 0) {
                throw Exception(FXHelper.getBundle().getString("sys.person.dataName"))
            }
        }


        // name is empty
        if(person.firstName.isEmpty()) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataFirstName"))
        }

        if(person.lastName.isEmpty()) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataLastName"))
        }

        // gender is empty
        if(!(person.gender == 0 || person.gender == 1 || person.gender == 2)) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataGender"))
        }

        // birthday not null or in future
        if(person.birthDate == null) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataBirthDay"))
        } else {
            val current = Date()
            if(current.before(person.birthDate)) {
                throw Exception(FXHelper.getBundle().getString("sys.person.dataBirthDayInPast"))
            }
        }

        // save
        if(person.id == 0) {
            return this.peopleMapper?.insertPerson(person) ?: 0
        } else {
            this.peopleMapper?.updatePerson(person)
            return person.id
        }
    }

    @Throws(Exception::class)
    fun deletePerson(person: Person) {
        this.peopleMapper?.deletePerson(person)
    }
}